/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.FinancialDAO;
import dal.NotificationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import models.*;

public class DF_MemberIncomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int clubID = (int) request.getSession().getAttribute("clubID");
        Term term = (Term) request.getSession().getAttribute("term");

        // Lấy incomeID đầu tiên nếu chưa truyền tham số
        int incomeID = FinancialDAO.getIncomeIDPending(clubID, term.getTermID());
        String incomeParam = request.getParameter("incomeID");
        if (incomeParam != null && !incomeParam.isEmpty()) {
            incomeID = Integer.parseInt(incomeParam);
        }
        // Xử lý các hành động
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "markPaid":
                    int contributionID = Integer.parseInt(request.getParameter("contributionID"));
                    if (FinancialDAO.markContributionPaid(contributionID)) {
                        request.setAttribute("message", "Đã cập nhật trạng thái đóng phí thành công!");
                    } else {
                        request.setAttribute("error", "Không thể cập nhật trạng thái đóng phí!");
                    }
                    break;

                case "remindAll":
                    if (sentToMemberPendingPaid(request, response, clubID, term.getTermID(), incomeID)) {

                        request.setAttribute("message", "Đã gửi thông báo nhắc nhở tới tất cả thành viên chưa đóng phí!");

                    } else {
                        request.setAttribute("error", "Không thể gửi thông báo nhắc nhở!");
                    }
                    break;
                case "complete":
                    if (FinancialDAO.completeIncome(incomeID)) {
                        request.setAttribute("message", "Đã hoàn thành thu phí thành viên!");
                    } else {
                        request.setAttribute("error", "Không thể hoàn thành thu phí thành viên!");
                    }
                    break;
            }
        }

        // Lấy tham số lọc
        String keyword = request.getParameter("keyword");
        String status_raw = request.getParameter("status");
        String status = "all";
        if (status_raw != null && !status_raw.isEmpty()) {
            status = request.getParameter("status");
        }
        // Phân trang
        int page = 1;
        int pageSize = 7;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Lấy dữ liệu phân trang và tổng số bản ghi
        List<MemberIncomeContributions> pagedList = FinancialDAO.IncomeMemberSrc(incomeID, keyword, status, page, pageSize);
        int totalRecords = FinancialDAO.getTotalIncomeMemberRecords(incomeID, keyword, status);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        List<Income> incomeIDs = FinancialDAO.getIncomeMemberPendings(clubID, term.getTermID());

        // Kiểm tra trạng thái nút
        boolean allPaid = FinancialDAO.areAllContributionsPaid(incomeID);
        boolean hasPending = FinancialDAO.hasPendingContributions(incomeID);

        // Truyền dữ liệu sang JSP
        request.setAttribute("IncomeMemberSrc", pagedList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("incomeID", incomeID);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("incomeIDs", incomeIDs);
        request.setAttribute("allPaid", allPaid);
        request.setAttribute("hasPending", hasPending);
        request.getRequestDispatcher("/view/student/department-leader/financial-income-member.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Tạm thời gọi doGet để xử lý
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing member income contributions";
    }

    private boolean sentToMemberPendingPaid(HttpServletRequest request, HttpServletResponse response, int clubID, String termID, int incomeID) {
        List<MemberIncomeContributions> UserUnpaidInComeIDs = FinancialDAO.getUserUnpaidInComeIDs(incomeID);
        for (MemberIncomeContributions UserUnpaidInComeID : UserUnpaidInComeIDs) {
            String content = String.format(
                    "Bạn hiện chưa hoàn thành đóng phí thành viên cho kỳ hạn %s với số tiền là %,d₫. "
                    + "Hạn chót nộp là ngày %1$td/%1$tm/%1$tY. "
                    + "Vui lòng hoàn tất thanh toán đúng hạn để đảm bảo quyền lợi thành viên.",
                    UserUnpaidInComeID.getDueDate(), // java.sql.Timestamp
                    UserUnpaidInComeID.getAmount().intValue()
            );
            NotificationDAO.sentToPerson1(null, UserUnpaidInComeID.getUserID(), "Thông báo về việc đóng phí thành viên", content, "High");
        }
        return true;
    }
}

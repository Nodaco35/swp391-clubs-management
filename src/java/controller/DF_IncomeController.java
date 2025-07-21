/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.DepartmentDashboardDAO;
import dal.FinancialDAO;
import dal.TermDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import models.Income;
import models.Term;
import models.Users;

public class DF_IncomeController extends HttpServlet {

    private DepartmentDashboardDAO dashboardDAO;

    @Override
    public void init() throws ServletException {
        dashboardDAO = new DepartmentDashboardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int clubID = (int) request.getSession().getAttribute("clubID");
        Term term = TermDAO.getActiveSemester();
        if (term.getTermID() == null) {
            request.setAttribute("error", "Hiện chưa có kì nào hoạt động nên không cần quản lý tài chính");
            request.getRequestDispatcher("/myclub").forward(request, response);
            return;
        }

        if (!dashboardDAO.isDepartmentLeaderIndoingoai(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }

        String status = request.getParameter("status");
        String source = request.getParameter("source");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 7;

        List<Income> incomes = FinancialDAO.getIncomesByClubAndTerm(clubID, term.getTermID(), status, source, page, pageSize);
        int totalIncomes = FinancialDAO.getTotalIncomes(clubID, term.getTermID(), status, source);
        int totalPages = (int) Math.ceil((double) totalIncomes / pageSize);

        request.setAttribute("incomes", incomes);
        request.setAttribute("clubID", clubID);
        request.setAttribute("termID", term.getTermID());
        request.setAttribute("status", status);
        request.setAttribute("source", source);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/view/student/department-leader/financial-income.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int clubID = (int) request.getSession().getAttribute("clubID");
        Term term = TermDAO.getActiveSemester();
        if (term.getTermID() == null) {
            request.setAttribute("error", "Hiện chưa có kì nào hoạt động nên không cần quản lý tài chính");
            request.getRequestDispatcher("/myclub").forward(request, response);
            return;
        }

        if (!dashboardDAO.isDepartmentLeaderIndoingoai(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }
        switch (action) {
            case "paid":
                int incomeID = Integer.parseInt(request.getParameter("incomeID"));
                boolean success = FinancialDAO.updateIncomeStatus(incomeID, user.getUserID());
                if (success) {
                    request.setAttribute("message", "Cập nhật trạng thái thành công");
                } else {
                    request.setAttribute("error", "Cập nhật trạng thái thất bại");
                }
                break;
            case "insert":
                try {
                String source = request.getParameter("source");
                String amountParam = request.getParameter("amount");
                BigDecimal amount = new BigDecimal(amountParam);
                String dueDateStr = request.getParameter("dueDate");
                String description = request.getParameter("description");
                String status = "Đang chờ";

                // Validate required fields
                if (source == null || source.trim().isEmpty() || (amount.compareTo(BigDecimal.ZERO) < 0)) {
                    request.setAttribute("error", "Nguồn và số tiền là bắt buộc và phải hợp lệ");
                    doGet(request, response);
                    return;
                }
                String formattedStartedTime = "";
                // Validate amount for "Phí thành viên"
                if (source.equals("Phí thành viên")) {
                    if (dueDateStr == null || dueDateStr.trim().isEmpty()) {
                        request.setAttribute("error", "Bạn phải nhập hạn chót (Due Date).");
                        doGet(request, response);
                        return;
                    }

                    formattedStartedTime = dueDateStr.replace("T", " ") + ":00"; // ← Dòng này cần được chạy!
                    
                    int memberCount = dashboardDAO.getClubMemberCount(clubID);
                    BigDecimal minimumAmount = BigDecimal.valueOf(memberCount * 10000);
                    
                    if (amount.compareTo(minimumAmount) < 0) {
                        request.setAttribute("error", "Số tiền phí thành viên phải lớn hơn hoặc bằng " + minimumAmount + " (" + memberCount + " x 10)");
                        doGet(request, response);
                        return;
                    }

                } else {
                    status = request.getParameter("status");
                }
                BigDecimal minimumAmount = BigDecimal.valueOf(100000);

                if (amount.compareTo(minimumAmount) < 0) {
                    request.setAttribute("error", "Số tiền không hợp lệ! ít nhất là" + minimumAmount);
                    doGet(request, response);
                    return;
                }

                Income income = new Income();
                income.setClubID(clubID);
                income.setTermID(term.getTermID());
                income.setSource(source);
                income.setAmount(amount);
                income.setDescription(description != null ? description.trim() : "");
                income.setStatus(status);

                if (FinancialDAO.insertIncome(income, formattedStartedTime, user.getUserID())) {
                    request.setAttribute("message", "Thêm nguồn thu thành công");
                } else {
                    request.setAttribute("error", "Thêm nguồn thu thất bại");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Số tiền không hợp lệ");
            } catch (Exception e) {
                request.setAttribute("error", "Đã xảy ra lỗi khi thêm nguồn thu: " + e.getMessage());
            }
            break;
        }
        doGet(request, response);
    }
}

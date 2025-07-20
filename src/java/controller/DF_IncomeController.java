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
        int pageSize = 10;

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
        }
        doGet(request, response);
    }
}

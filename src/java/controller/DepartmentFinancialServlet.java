/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.DepartmentDashboardDAO;
import dal.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import models.*;

public class DepartmentFinancialServlet extends HttpServlet {

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

        String compIncomeWithPreTerm = "";
        String compExpensesWithPreTerm = "";
        String compBalanceWithPreTerm = "";
        BigDecimal totalIncomePre = FinancialDAO.getTotalIncomeAmount(clubID, FinancialDAO.getPreviousTermID(term.getTermID()));
        BigDecimal totalIncome = FinancialDAO.getTotalIncomeAmount(clubID, term.getTermID());
        BigDecimal totalExpensesPre = FinancialDAO.getTotalExpenseAmount(clubID, FinancialDAO.getPreviousTermID(term.getTermID()));
        BigDecimal totalExpenses = FinancialDAO.getTotalExpenseAmount(clubID, term.getTermID());
        BigDecimal balance = totalIncome.subtract(totalExpenses);
        BigDecimal balancePre = totalIncomePre.subtract(totalExpensesPre);

        BigDecimal incomeMemberPending = FinancialDAO.getTotalIncomeMemberPending(clubID, term.getTermID());
        double comp = totalIncome.divide(totalIncomePre, 4, RoundingMode.HALF_UP).doubleValue();
        double comp2 = totalExpenses.divide(totalExpensesPre, 4, RoundingMode.HALF_UP).doubleValue();
        double comp3 = balance.divide(balancePre, 4, RoundingMode.HALF_UP).doubleValue();
        if (comp >= 1) {
            compIncomeWithPreTerm = "+" + comp + "%";
        } else {
            compIncomeWithPreTerm = "-" + comp + "%";
        }

        if (comp2 >= 1) {
            compIncomeWithPreTerm = "+" + comp2 + "%";
        } else {
            compExpensesWithPreTerm = "-" + comp2 + "%";
        }
        if (comp3 >= 1) {
            compBalanceWithPreTerm = "+" + comp3 + "%";
        } else {
            compBalanceWithPreTerm = "-" + comp3 + "%";
        }

        int memberPendingIncome = FinancialDAO.getTotalIncomePersonByType(clubID, term.getTermID(), "Pending");
        int totalMember = FinancialDAO.getTotalIncomePersonByType(clubID, term.getTermID(), "");
        int totalPaidMember = FinancialDAO.getTotalIncomePersonByType(clubID, term.getTermID(), "Paid");

        List<MemberIncomeContributions> previewIncomeMemberSrc = FinancialDAO.getPreviewIncomeMemberSrc(clubID, term.getTermID());
        List<Transaction> recentTransactions = FinancialDAO.getRecentTransactions(clubID, term.getTermID());
        request.setAttribute("term", term);
        request.setAttribute("compIncomeWithPreTerm", compIncomeWithPreTerm);
        request.setAttribute("compExpensesWithPreTerm", compExpensesWithPreTerm);
        request.setAttribute("totalIncome", totalIncome);
        request.setAttribute("totalExpenses", totalExpenses);
        request.setAttribute("balance", balance);
        request.setAttribute("compBalanceWithPreTerm", compBalanceWithPreTerm);
        request.setAttribute("incomeMemberPending", incomeMemberPending);
        request.setAttribute("memberPendingIncome", memberPendingIncome);
        request.setAttribute("totalMember", totalMember);
        request.setAttribute("totalPaidMember", totalPaidMember);
        request.setAttribute("previewIncomeMemberSrc", previewIncomeMemberSrc);
        request.setAttribute("recentTransactions", recentTransactions);

        request.getRequestDispatcher("/view/student/department-leader/financial.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DepartmentFinancialServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DepartmentFinancialServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

package controller;

import dal.ExpenseDAO;
import dal.FinancialDAO;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Clubs;
import models.Transaction;
import models.Users;


public class FinancialManagementServlet extends HttpServlet {
    private FinancialDAO financialDAO;
    private ExpenseDAO expenseDAO;

    @Override
    public void init() throws ServletException {
        financialDAO = new FinancialDAO();
        expenseDAO = new ExpenseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        Clubs club = (Clubs) session.getAttribute("club");

//        if (user == null || club == null || !user.getRole().equals("Chairman")) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }

        int clubID = club.getClubID();
        int page = 1;
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        int pageSize = 10; // Number of transactions per page
        String search = request.getParameter("search");
        String type = request.getParameter("type");
        String termID = request.getParameter("termID");

        // Default values
        if (search == null) search = "";
        if (type == null) type = "all";
        if (termID == null) termID = "all";

        try {
            // Get completed transactions (income and/or expenses based on type)
            List<Transaction> transactions = new ArrayList<>();
            int totalTransactions = 0;

            if (type.equals("all") || type.equals("income")) {
                transactions.addAll(financialDAO.getCompletedTransactions(clubID, search, termID, page, pageSize));
                totalTransactions += financialDAO.countCompletedTransactions(clubID, search, termID);
            }
            if (type.equals("all") || type.equals("expense")) {
                transactions.addAll(expenseDAO.getCompletedTransactions(clubID, search, termID, page, pageSize));
                totalTransactions += expenseDAO.countCompletedTransactions(clubID, search, termID);
            }

            // Sort transactions by date (descending)
            transactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

            // Adjust for pagination across merged results
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, transactions.size());
            if (startIndex < transactions.size()) {
                transactions = transactions.subList(startIndex, endIndex);
            } else {
                transactions = new ArrayList<>();
            }

            int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);

            // Calculate financial overview
            double totalIncome = financialDAO.getTotalIncome(clubID, termID);
            double totalExpense = expenseDAO.getTotalExpense(clubID, termID);
            double totalPendingIncome = financialDAO.getTotalPendingIncome(clubID, termID);
            List<String> termIDs = financialDAO.getAllTermIDs(clubID);

            // Set attributes for JSP
            request.setAttribute("transactions", transactions);
            request.setAttribute("totalIncome", totalIncome);
            request.setAttribute("totalExpense", totalExpense);
            request.setAttribute("totalPendingIncome", totalPendingIncome);
            request.setAttribute("termIDs", termIDs);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/view/chairman/financial-management.jsp").forward(request, response);
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Lỗi khi tải dữ liệu tài chính: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/chairman-page/financial-management");
        }
    }
}
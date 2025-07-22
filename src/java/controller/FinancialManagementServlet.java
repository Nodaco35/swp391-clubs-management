package controller;

import dal.ClubDAO;
import dal.FinancialDAO;
import dal.TermDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.ClubInfo;
import models.Transaction;
import models.Users;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import models.Term;

public class FinancialManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        ClubDAO clubDAO = new ClubDAO();
        FinancialDAO financialDAO = new FinancialDAO();
        String userID = user.getUserID();
        ClubInfo club = clubDAO.getClubChairman(userID);
        int clubID = clubDAO.getClubIDByUserID(userID);

        if (club == null || clubID == 0) {
            request.setAttribute("errorMessage", "Bạn không có quyền truy cập trang này.");
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
            return;
        }

        // Get parameters
        String search = request.getParameter("search");
        Term term = TermDAO.getActiveSemester();
        String termID = term.getTermID();
        if(request.getParameter("termID") != null){
             termID = request.getParameter("termID");
        }
        String type = request.getParameter("type");
        String pageStr = request.getParameter("page");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = 10;

       
        if (type == null || type.trim().isEmpty()) {
            type = "all";
        }

        // Get completed transactions with search and type filter
        List<Transaction> transactions = financialDAO.getCompletedTransactions(clubID, search, termID, type, page, pageSize);
        int totalTransactions = financialDAO.countCompletedTransactions(clubID, search, termID, type);
        int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);

        // Get financial overview data
        double totalIncome = financialDAO.getTotalIncome(clubID, termID);
        double totalExpense = financialDAO.getTotalExpense(clubID, termID);
        BigDecimal currentBalance = financialDAO.getClubBalance(clubID, termID);
        BigDecimal totalPendingIncome = financialDAO.getTotalIncomeMemberPending(clubID, termID);
        List<String> termIDs = financialDAO.getAllTermIDs(clubID);

        // Set attributes for JSP
        session.setAttribute("club", club);
        request.setAttribute("transactions", transactions);
        request.setAttribute("totalIncome", totalIncome);
        request.setAttribute("totalExpense", totalExpense);
        request.setAttribute("currentBalance", currentBalance);
        request.setAttribute("totalPendingIncome", totalPendingIncome);
        request.setAttribute("termIDs", termIDs);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPath", request.getServletPath());
        request.setAttribute("search", search);
        request.setAttribute("type", type);
        request.setAttribute("termID", termID);

        // Forward to JSP
        request.getRequestDispatcher("/view/student/chairman/financial-management.jsp").forward(request, response);
    }
}
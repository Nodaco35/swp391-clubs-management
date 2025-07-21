/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.FinancialDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import models.Clubs;
import models.Term;
import models.Transaction;
import models.Users;

public class TransactionController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String transResult = "";
        String termID = request.getParameter("termID");
        String clubID = request.getParameter("clubID");
        
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        if (request.getAttribute("transResult") != null) {
            transResult = (String) request.getAttribute("transResult");
        }
        if(request.getParameter("transResult") != null){
            transResult = request.getParameter("transResult");
        }
        int page = 1;
        int pagesize = 10;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }


        List<Transaction>  transactions = FinancialDAO.getTransactionsByUser(user.getUserID(), transResult, termID, clubID, page);
        int totalRecords = FinancialDAO.getTotalTransactionCount(user.getUserID(), transResult, termID, clubID);
        int totalPages = (int) Math.ceil((double) totalRecords / pagesize);
        List<Term> terms = FinancialDAO.getAllTerms();
        List<Clubs> clubs = FinancialDAO.getClubsByUser(user.getUserID());
        request.setAttribute("transactions", transactions);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("transResult", transResult);
        request.setAttribute("termID", termID);
        request.setAttribute("clubID", clubID);
        request.setAttribute("terms", terms);
        request.setAttribute("clubs", clubs);

        request.getRequestDispatcher("/view/student/member/transaction-history.jsp").forward(request, response);
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
            out.println("<title>Servlet TransactionController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TransactionController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

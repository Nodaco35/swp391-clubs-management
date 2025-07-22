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
import models.Term;
import models.Transaction;
import models.Users;


public class DF_TransactionController extends HttpServlet {
   

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
        
        String type = request.getParameter("type");
        String status = request.getParameter("status");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 7;

        List<Transaction> transactions = FinancialDAO.getTransactionsByClubAndTerm(clubID, term.getTermID(), type, status, page, pageSize);
        int totalTransactions = FinancialDAO.getTotalTransactions(clubID, term.getTermID(), type, status);
        int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);

        request.setAttribute("transactions", transactions);
        request.setAttribute("clubID", clubID);
        request.setAttribute("termID", term.getTermID());
        request.setAttribute("type", type);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/view/student/department-leader/financial-transaction-club-history.jsp").forward(request, response);
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
            out.println("<title>Servlet DF_TransactionController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DF_TransactionController at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    


    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

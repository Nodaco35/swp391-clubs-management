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
import models.MemberIncomeContributions;
import models.Users;


public class InvoiceServlet extends HttpServlet {
   

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String error = "";
        if (request.getAttribute("error") != null) {
            error = (String) request.getAttribute("error");
        }
        
        String userID = user.getUserID();
        String status = request.getParameter("status") != null ? request.getParameter("status") : "Pending"; // Mặc định Pending
        String termID = request.getParameter("termID");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 10; // 10 hóa đơn mỗi trang

        List<MemberIncomeContributions> invoices = FinancialDAO.getInvoicesByUserID(userID, status, termID, page, pageSize);
        int totalInvoices = FinancialDAO.getTotalInvoices(userID, status, termID);
        int totalPages = (int) Math.ceil((double) totalInvoices / pageSize);

        request.setAttribute("invoices", invoices);
        request.setAttribute("userID", userID);
        request.setAttribute("status", status);
        request.setAttribute("termID", termID);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // Lấy danh sách TermID để lọc
        List<String> termIDs = FinancialDAO.getTermIDs();
        request.setAttribute("termIDs", termIDs);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/view/student/member/invoices.jsp").forward(request, response);
    } 

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet InvoiceServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet InvoiceServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    


    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

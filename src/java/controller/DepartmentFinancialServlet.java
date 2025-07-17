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
        int clubID = (int) request.getSession().getAttribute("clubID");
        Term term = TermDAO.getActiveSemester(); 
        if (term.getTermID() == null) {
            request.setAttribute("error", "Hiện chưa có kì nào hoạt động nên không cần quản lý tài chính");
            request.getRequestDispatcher("/myclub").forward(request, response);
            return;
        }
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!dashboardDAO.isDepartmentLeaderIndoingoai(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }
        
        
        
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

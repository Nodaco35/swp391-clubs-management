/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;

/**
 *
 * @author Admin
 */
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            request.getRequestDispatcher("view/homePage.jsp").forward(request, response);
            return;
        } else if (action.equals("profile")) {
            request.getRequestDispatcher("view/profile.jsp").forward(request, response);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("update")) {
            update(request, response);
        }

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet HomeController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HomeController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String newName = request.getParameter("name");
        String email = request.getParameter("email");
        String id = request.getParameter("id");
        String msg;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        // bắt lỗi email
        if (email.endsWith("@gmail.com") || email.endsWith("@fpt.edu.vn")) {
            UserDAO.update(newName, email, id);
            msg = "cập nhập thành công";
            request.setAttribute("msg", msg);
            
            user.setFullName(newName);
            user.setEmail(email);
            session.setAttribute("user", user);
            request.getRequestDispatcher("view/profile.jsp").forward(request, response);
            return;
        }

        msg = "gmail không hợp lệ";
        request.setAttribute("msg", msg);
        request.getRequestDispatcher("view/profile.jsp").forward(request, response);
        return;
    }

}

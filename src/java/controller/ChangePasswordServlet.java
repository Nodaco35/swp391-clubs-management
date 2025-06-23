/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import models.Users;

/**
 *
 * @author Admin
 */
@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method, hiển thị form đổi mật khẩu
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        
        // Hiển thị trang đổi mật khẩu
        request.getRequestDispatcher("view/change-password.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method, xử lý việc đổi mật khẩu
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        String userID = user.getUserID();
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Kiểm tra xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("msg", "Xác nhận mật khẩu không đúng");
            request.setAttribute("msgType", "error");
            request.getRequestDispatcher("view/change-password.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra mật khẩu hiện tại có đúng không
        if (!UserDAO.checkPassword(userID, currentPassword)) {
            request.setAttribute("msg", "Mật khẩu hiện tại không đúng");
            request.setAttribute("msgType", "error");
            request.getRequestDispatcher("view/change-password.jsp").forward(request, response);
            return;
        }        // Cập nhật mật khẩu mới
        boolean success = UserDAO.changePassword(userID, newPassword);
        
        if (!success) {
            request.setAttribute("msg", "Có lỗi xảy ra khi đổi mật khẩu");
            request.setAttribute("msgType", "error");
            request.getRequestDispatcher("view/change-password.jsp").forward(request, response);
            return;
        }

        // Đặt thông báo thành công vào session để hiển thị trên trang profile
        // Dùng session đã được khai báo ở đầu phương thức
        session.setAttribute("msg", "Đổi mật khẩu thành công");
        session.setAttribute("msgType", "success");
        
        // Chuyển hướng người dùng về trang profile
        response.sendRedirect("profile?action=myProfile");
    }

    @Override
    public String getServletInfo() {
        return "Servlet quản lý việc đổi mật khẩu";
    }
}

package controllers;

import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.User;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị trang đăng nhập
        request.getRequestDispatcher("view/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thông tin đăng nhập từ form
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        PrintWriter out = response.getWriter();
        boolean isValid = false;
        List<User> userList;
        User validUser = null;
        try {
            userList = UserDAO.getAllUsers();
            
            for (User user : userList) {
                if (user.getEmail().equals(email)&&user.getPassword().equals(password)) {
                    isValid = true;
                    validUser = user;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (isValid) {

            // Tạo session
            HttpSession session = request.getSession();
            session.setAttribute("user", validUser);

            // Chuyển hướng đến trang chủ - HomepageServlet
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            // Thông báo lỗi đăng nhập
            request.setAttribute("error", "Email hoặc mật khẩu không đúng");
            request.getRequestDispatcher("view/login.jsp").forward(request, response);
        }

    }
}

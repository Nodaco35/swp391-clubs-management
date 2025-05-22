/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Random;
import models.User;
import util.Email;

/**
 *
 * @author Admin
 */
public class OtpController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "sendOtp":
                sendOtp(request, response);
                break;
            default:
                throw new AssertionError();
        }
    }

    private void sendOtp(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String email = request.getParameter("email");

        // Tạo mã OTP ngẫu nhiên
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        session.setAttribute("otp", otp);
        session.setAttribute("otpEmail", email);

        // Gửi email OTP
        String subject = "Verify OTP Email";
        String content = "Mã OTP của bạn là: <b>" + otp + "</b>. Vui lòng sử dụng mã này để xác nhận thay đổi email.";
        Email.sendEmail(email, subject, content);
        session.setAttribute("user", user);
        request.getRequestDispatcher("view/verifyCode.jsp").forward(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet OtpController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet OtpController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

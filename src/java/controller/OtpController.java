/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Random;

import models.Users;
import util.Email;

/**
 *
 * @author Admin
 */
public class OtpController extends HttpServlet {
    private int cnt = 0;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            resendOtp(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "sendOtp":
                sendOtp(request, response);
                break;
            case "confirmOtp":
                confirmAndRemoveOtp(request, response);

                break;
            case "resendOtp":
                resendOtp(request, response);
                break;
            default:
                throw new AssertionError();
        }
    }

    private void sendOtp(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        cnt = 0;
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String email = request.getParameter("email");
        String type = request.getParameter("type");
        String msg = "Email đã tồn tại!";
        // Tạo mã OTP ngẫu nhiên
        session.removeAttribute("otp");
        session.removeAttribute("otpEmail");
        session.removeAttribute("type");
        if (type.equals("Verify new email")) {
            if (UserDAO.getUserByEmail(email) != null) {
                request.setAttribute("error", msg);
                request.getRequestDispatcher("view/secure/editEmail.jsp").forward(request, response);
                return;
            }
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        session.setAttribute("otp", otp);
        session.setAttribute("otpEmail", email);
        session.setAttribute("type", type);

        // Gửi email OTP
        String subject = "Verify OTP Email";
        String content = "Mã OTP của bạn là: <b>" + otp + "</b>. Vui lòng sử dụng mã này để xác nhận thay đổi email.";
        Email.sendEmail(email, subject, content);
        session.setAttribute("user", user);
        request.getRequestDispatcher("view/secure/verifyCode.jsp").forward(request, response);
    }

    private void confirmAndRemoveOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String otpInput = request.getParameter("otp");
        String otp = (String) session.getAttribute("otp");
        String type = (String) session.getAttribute("type");
        String otpEmail = (String) session.getAttribute("otpEmail");
        String msg;

        if (type.equals("Verify current email")) {
            if (otpInput.equals(otp)) {
                session.removeAttribute("otp");
                session.removeAttribute("otpEmail");
                session.removeAttribute("type");
                session.setAttribute("user", user);
                request.getRequestDispatcher("view/secure/editEmail.jsp").forward(request, response);
                return;
            }
        } else if (type.equals("Verify new email")) {
            if (otpInput.equals(otp)) {
                String id = request.getParameter("id");

                UserDAO.updateEmail(otpEmail, id);
                msg = "Cập nhập thành công";
                request.setAttribute("msg", msg);
                session.removeAttribute("otp");
                session.removeAttribute("otpEmail");
                session.removeAttribute("type");
                user = UserDAO.getUserById(id);
                session.setAttribute("user", user);
                request.getRequestDispatcher("view/profile.jsp").forward(request, response);
                return;
            }
        }
        cnt++;
        if (cnt == 4) {
            session.removeAttribute("user");
            response.sendRedirect("index.jsp");
            return;
        }
        msg = "Mã OTP không khớp(hãy thực hiện gửi lại nếu nhập sai quá 3 lần sẽ đăng xuất tài khoản)!";
        request.setAttribute("msg", msg);
        request.getRequestDispatcher("view/secure/verifyCode.jsp").forward(request, response);

    }

    private void resendOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cnt = 0;
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("otpEmail");
        String type = (String) session.getAttribute("type");
        Users user = (Users) session.getAttribute("user");
        session.removeAttribute("otp");

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000); // 6 số
        session.setAttribute("otp", otp);

        String subject = "Verify OTP Email";
        String content = "Mã OTP của bạn là: <b>" + otp + "</b>. Vui lòng sử dụng mã này để xác nhận thay đổi email.";
        Email.sendEmail(email, subject, content);
        session.setAttribute("user", user);
        session.setAttribute("type", type);
        request.setAttribute("msg", "Mã OTP đã được gửi lại tới email: " + email);
        request.getRequestDispatcher("view/secure/verifyCode.jsp").forward(request, response);
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
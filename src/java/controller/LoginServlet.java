package controller;

import dal.UserDAO;
import models.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author NC PC
 */
public class LoginServlet extends HttpServlet {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@fpt\\.edu\\.vn$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("view/login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Kiểm tra input
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu!");
            request.setAttribute("email", email);
            request.getRequestDispatcher("view/login.jsp").forward(request, response);
            return;
        }

        // Kiểm tra định dạng email
        if (!pattern.matcher(email).matches()) {
            request.setAttribute("error", "Email không hợp lệ (phải dùng @fpt.edu.vn)!");
            request.setAttribute("email", email);
            request.getRequestDispatcher("view/login.jsp").forward(request, response);
            return;
        }

        // Xác thực người dùng
        UserDAO ud = new UserDAO();
        Users user_find = ud.getUserByEmailAndPassword(email.trim(), password.trim());
        if (user_find == null) {
            request.setAttribute("error", "Sai tài khoản hoặc mật khẩu");
            request.setAttribute("email", email);
            request.getRequestDispatcher("view/login.jsp").forward(request, response);
        } else if (!user_find.isStatus()) {
            // Kiểm tra nếu tài khoản chưa xác minh
            request.setAttribute("error", "Tài khoản chưa được xác minh. Vui lòng kiểm tra email để xác minh tài khoản.");
            request.setAttribute("unverifiedEmail", email);

            // Thêm phần HTML cho nút gửi lại email xác minh
            String resendButton = "<div class='resend-verification'>" +
                    "<p>Không nhận được email? <a href='" + request.getContextPath() +
                    "/resend-verification?email=" + email + "'>Gửi lại email xác minh</a></p></div>";
            request.setAttribute("resendVerification", resendButton);

            request.getRequestDispatcher("view/login.jsp").forward(request, response);
        } else {
            // Tạo session
            HttpSession session = request.getSession();
            session.setAttribute("user", user_find);

            session.setAttribute("fullName", user_find.getFullName());
            session.setAttribute("permissionID", user_find.getPermissionID());
            session.setAttribute("userID", user_find.getUserID());
            session.setMaxInactiveInterval(30 * 60); // 30 phút

            List<Integer> myEventIDs = ud.getEventIDsOfChairman(user_find.getUserID());
            int myClubID = ud.getClubIdIfChairman(user_find.getUserID());
            boolean isChairman = ud.isChairman(user_find.getUserID(), myClubID);
            session.setAttribute("myClubID", myClubID);
            session.setAttribute("myEventIDs", myEventIDs);
            session.setAttribute("isChairman", isChairman);


            session.setAttribute("isChairman", isChairman);
            // Điều hướng theo quyền
            if (user_find.getPermissionID() == 1) {
                // Chuyển hướng đến trang chủ - HomepageServlet
                response.sendRedirect(request.getContextPath() + "/");
            } else if (user_find.getPermissionID() == 2) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else if (user_find.getPermissionID() == 3) {
                response.sendRedirect(request.getContextPath() + "/ic");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "LoginServlet handles user authentication";
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.User;

/**
 *
 * @author Admin
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 5 * 1024 * 1024, // 5MB
        maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class ProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else if (action.equals("myProfile")) {
            request.getRequestDispatcher("view/profile.jsp").forward(request, response);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "update": {
                try {
                    update(request, response);
                } catch (ParseException ex) {
                    Logger.getLogger(ProfileController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;

            default:
                throw new AssertionError();
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

    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        String id = user.getUserID();
        String email = user.getEmail();
        String newName = request.getParameter("name");
        String avatarPath = user.getAvatar();

        String dob = request.getParameter("dob"); // Ví dụ: "2023-12-25"

        // Xử lý file ảnh nếu có
        Part filePart = request.getPart("avatar");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String contentType = filePart.getContentType().toLowerCase();

            // Kiểm tra file có phải ảnh hợp lệ
            if (!contentType.startsWith("image/")
                    || !(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                    || fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".webp"))) {
                request.setAttribute("msg", "Chỉ được upload file ảnh (.jpg, .png, .gif, .webp)");
                request.setAttribute("msgType", "error");
                request.getRequestDispatcher("view/profile.jsp").forward(request, response);
                return;
            }

            // Tạo tên file mới
            String ext = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + ext;

            // Xác định đường dẫn ghi ảnh (trong web/img)
            String folder = "img";
            String buildPath = getServletContext().getRealPath("/");
            String webPath = buildPath.replace("build" + File.separator + "web", "web") + folder; // Đường dẫn tới web/img
            String buildImgPath = buildPath + folder; // Đường dẫn tới build/web/img

            // Tạo thư mục web/img nếu chưa tồn tại
            File webDir = new File(webPath);
            if (!webDir.exists()) {
                webDir.mkdirs();
            }

            // Tạo thư mục build/web/img nếu chưa tồn tại
            File buildDir = new File(buildImgPath);
            if (!buildDir.exists()) {
                buildDir.mkdirs();
            }

            // Lưu ảnh vào web/img
            String webUploadPath = webPath + File.separator + newFileName;
            try {
                filePart.write(webUploadPath);

                // Sao chép ảnh sang build/web/img
                String buildUploadPath = buildImgPath + File.separator + newFileName;
                Files.copy(new File(webUploadPath).toPath(), new File(buildUploadPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

                avatarPath = folder + "/" + newFileName;
            } catch (IOException e) {
                e.printStackTrace();
                request.setAttribute("msg", "Lỗi khi upload ảnh: " + e.getMessage());
                request.setAttribute("msgType", "error");
                request.getRequestDispatcher("view/profile.jsp").forward(request, response);
                return;
            }
        }

        // Cập nhật thông tin vào DB
        UserDAO.update(newName, avatarPath, dob, id);

        // Cập nhật lại session user
        User updatedUser = UserDAO.getUserById(id);
        session.setAttribute("user", updatedUser);

        request.setAttribute("msg", "Cập nhật thành công");
        request.setAttribute("msgType", "success");
        request.getRequestDispatcher("view/profile.jsp").forward(request, response);
    }

}

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
import java.nio.file.Paths;
import models.User;
import util.Email;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Admin
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 5 * 1024 * 1024, // 5MB
        maxRequestSize = 10 * 1024 * 1024 // 10MB
)
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

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    
    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        

        String id = user.getUserID();
        String email = user.getEmail();
        String newName = request.getParameter("name");
        String avatarPath = user.getAvatar(); // Giữ ảnh cũ nếu không upload mới

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
                request.getRequestDispatcher("view/profile.jsp").forward(request, response);
                return;
            }

            // Tạo tên file mới
            String ext = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + ext;

            // Xác định đường dẫn ghi ảnh (trong build/web/img)
            String folder = "img";
            String realPath = getServletContext().getRealPath("/") + folder;
            System.out.println(" Đường dẫn thư mục ảnh: " + realPath);

            File uploadDir = new File(realPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                System.out.println(" Thư mục chưa có, đã tạo mới: " + realPath);
            }

            String uploadPath = realPath + File.separator + newFileName;
            try {
                filePart.write(uploadPath);
                System.out.println(" Ghi ảnh thành công tại: " + uploadPath);
                avatarPath = folder + "/" + newFileName;
            } catch (IOException e) {
                System.out.println(" Lỗi ghi ảnh: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Cập nhật thông tin vào DB
        UserDAO.update(newName, avatarPath, id);
        System.out.println(" Đã cập nhật vào DB: " + newName + ", " + avatarPath);

        // Cập nhật lại session user
        User updatedUser = UserDAO.getUserById(id);
        session.setAttribute("user", updatedUser);

        request.setAttribute("msg", " Cập nhật thành công");
        request.getRequestDispatcher("view/profile.jsp").forward(request, response);
    }
    
    
    

}

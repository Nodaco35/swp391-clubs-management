package controller;

import dal.ClubDAO;
import dal.UserClubDAO;
import dal.PermissionDAO;
import dal.ClubCreationPermissionDAO;
import dal.CreatedClubApplicationsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import models.Clubs;
import models.Users;
import models.UserClub;
import models.CreatedClubApplications;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 5,  // 5MB
    maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class CreateClubServlet extends HttpServlet {

    private ClubDAO clubDAO;
    private UserClubDAO userClubDAO;
    private CreatedClubApplicationsDAO applicationDAO;
    private static final String UPLOAD_DIR = "img";
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final List<String> VALID_CATEGORIES = Arrays.asList("Thể Thao", "Học Thuật", "Phong Trào");

    @Override
    public void init() throws ServletException {
        clubDAO = new ClubDAO();
        userClubDAO = new UserClubDAO();
        applicationDAO = new CreatedClubApplicationsDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action != null && action.equals("editClub")) {
            handleEditClub(request, response);
        } else {
            handleCreateClubForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null && action.equals("updateClub")) {
            try {
                handleUpdateClub(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(CreateClubServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            handleCreateClub(request, response);
        }
    }

    private void handleCreateClubForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        boolean hasApplication = applicationDAO.hasActiveApplication(user.getUserID());
        CreatedClubApplications application = hasApplication ? applicationDAO.getActiveApplication(user.getUserID()) : null;
        String approvedClubName = application != null ? application.getClubName() : null;
        String approvedCategory = application != null ? application.getCategory() : null;
        List<models.Department> departments = clubDAO.getAllDepartments();
        request.setAttribute("hasPermission", hasApplication);
        request.setAttribute("approvedClubName", approvedClubName);
        request.setAttribute("approvedCategory", approvedCategory);
        request.setAttribute("departments", departments);
        request.setAttribute("isEdit", false);
        System.out.println("handleCreateClubForm: departments=" + departments + ", approvedClubName=" + approvedClubName + ", approvedCategory=" + approvedCategory);
        request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
    }

    private void handleEditClub(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        int clubID;
        try {
            clubID = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid club ID");
            return;
        }

        Clubs club = clubDAO.getClubById(clubID);
        if (club == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club not found");
            return;
        }

        UserClub userClub = userClubDAO.getUserClub(user.getUserID(), clubID);
        if (userClub == null || userClub.getRoleID() != 1) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa câu lạc bộ này.");
            request.setAttribute("isPresident", false);
            request.setAttribute("club", club);
            request.setAttribute("departments", clubDAO.getAllDepartments());
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        List<models.Department> departments = clubDAO.getAllDepartments();
        List<Integer> clubDepartmentIDs = clubDAO.getClubDepartmentIDs(clubID);
        request.setAttribute("club", club);
        request.setAttribute("departments", departments);
        request.setAttribute("clubDepartmentIDs", clubDepartmentIDs);
        request.setAttribute("isEdit", true);
        request.setAttribute("isPresident", true);
        System.out.println("handleEditClub: clubID=" + clubID + ", departments=" + departments + ", clubDepartmentIDs=" + clubDepartmentIDs);
        request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
    }

    private void handleCreateClub(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!applicationDAO.hasActiveApplication(user.getUserID())) {
            request.setAttribute("error", "Bạn không có quyền tạo câu lạc bộ. Vui lòng xin quyền trước.");
            request.setAttribute("hasPermission", false);
            request.setAttribute("departments", clubDAO.getAllDepartments());
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        CreatedClubApplications application = applicationDAO.getActiveApplication(user.getUserID());
        String clubName = application != null ? application.getClubName() : null;
        String category = application != null ? application.getCategory() : null;

        String description = request.getParameter("description");
        String contactPhone = request.getParameter("contactPhone");
        String contactGmail = request.getParameter("contactGmail");
        String contactURL = request.getParameter("contactURL");
        String establishedDateStr = request.getParameter("establishedDate");
        Part filePart = request.getPart("clubImg");
        String[] departmentIDs = request.getParameterValues("departmentIDs");

        System.out.println("Received create form data:");
        System.out.println("clubName: " + clubName);
        System.out.println("category: " + category);
        System.out.println("contactGmail: " + contactGmail);
        System.out.println("description: " + description);
        System.out.println("contactPhone: " + contactPhone);
        System.out.println("contactURL: " + contactURL);
        System.out.println("establishedDate: " + establishedDateStr);
        System.out.println("filePart: " + (filePart != null ? filePart.getName() + ", size: " + filePart.getSize() + ", type: " + filePart.getContentType() : "null"));
        System.out.println("departmentIDs: " + (departmentIDs != null ? Arrays.toString(departmentIDs) : "null"));

        List<String> errors = new ArrayList<>();
        if (clubName == null || clubName.trim().isEmpty()) {
            errors.add("Tên câu lạc bộ không được để trống.");
        } else if (clubDAO.isClubNameTaken(clubName, 0)) {
            errors.add("Tên câu lạc bộ '" + clubName + "' đã được sử dụng. Vui lòng chọn tên khác.");
        }
        if (category == null || !VALID_CATEGORIES.contains(category)) {
            errors.add("Danh mục không hợp lệ.");
        }
        if (contactGmail == null || contactGmail.trim().isEmpty()) {
            errors.add("Email liên hệ không được để trống.");
        }
        Date establishedDate = null;
        if (establishedDateStr != null && !establishedDateStr.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                establishedDate = new Date(sdf.parse(establishedDateStr).getTime());
            } catch (Exception e) {
                errors.add("Ngày thành lập không hợp lệ.");
            }
        }

        String relativePath = null;
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String contentType = filePart.getContentType().toLowerCase();

            if (!contentType.startsWith("image/") || 
                !(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                  fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".webp"))) {
                errors.add("Chỉ được upload file ảnh (.jpg, .png, .gif, .webp)");
            } else {
                String ext = fileName.substring(fileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + ext;

                String folder = UPLOAD_DIR;
                String buildPath = getServletContext().getRealPath("/");
                String webPath = buildPath.replace("build" + File.separator + "web", "web") + folder;
                String buildImgPath = buildPath + folder;

                File webDir = new File(webPath);
                if (!webDir.exists()) {
                    webDir.mkdirs();
                }
                File buildDir = new File(buildImgPath);
                if (!buildDir.exists()) {
                    buildDir.mkdirs();
                }

                String webUploadPath = webPath + File.separator + newFileName;
                try {
                    filePart.write(webUploadPath);
                    String buildUploadPath = buildImgPath + File.separator + newFileName;
                    Files.copy(new File(webUploadPath).toPath(), new File(buildUploadPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    relativePath = folder + "/" + newFileName;
                    System.out.println("Image saved successfully: " + relativePath);
                } catch (IOException e) {
                    errors.add("Lỗi khi upload ảnh: " + e.getMessage());
                }
            }
        } else {
            errors.add("Vui lòng chọn một hình ảnh.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("error", String.join("<br>", errors));
            request.setAttribute("hasPermission", true);
            request.setAttribute("approvedClubName", clubName);
            request.setAttribute("approvedCategory", category);
            request.setAttribute("departments", clubDAO.getAllDepartments());
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        Clubs club = new Clubs();
        club.setClubName(clubName);
        club.setDescription(description);
        club.setCategory(category);
        club.setClubImg(relativePath);
        club.setContactPhone(contactPhone);
        club.setContactGmail(contactGmail);
        club.setContactURL(contactURL);
        club.setEstablishedDate(establishedDate);
        club.setClubStatus(true);
        club.setIsRecruiting(true);

        List<Integer> departmentIDList = departmentIDs != null ?
                Arrays.stream(departmentIDs)
                      .filter(id -> id != null && !id.trim().isEmpty())
                      .map(Integer::parseInt)
                      .collect(Collectors.toList()) :
                new ArrayList<>();
        if (!departmentIDList.contains(3)) {
            departmentIDList.add(3);
        }

        try {
            int newClubID = clubDAO.createClub(club, departmentIDList);
            if (newClubID > 0) {
                applicationDAO.markApplicationAsUsed(user.getUserID());
                UserClub newUserClub = new UserClub();
                newUserClub.setUserID(user.getUserID());
                newUserClub.setClubID(newClubID);
                newUserClub.setRoleID(1);
                newUserClub.setClubDepartmentID(3);
                newUserClub.setIsActive(true);
                userClubDAO.addUserClub(newUserClub);
                Clubs createdClub = clubDAO.getClubById(newClubID);
                session.setAttribute("currentClub_" + newClubID, createdClub);
                System.out.println("Created club saved to session with ID: " + newClubID + ", ClubImg: " + createdClub.getClubImg());
                response.sendRedirect(request.getContextPath() + "/clubs");
                return;
            } else {
                request.setAttribute("error", "Tạo câu lạc bộ thất bại. Vui lòng kiểm tra email liên hệ (có thể đã tồn tại).");
            }
        } catch (SQLException e) {
            String errorMsg = "Lỗi khi tạo câu lạc bộ: " + e.getMessage();
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("ContactGmail")) {
                errorMsg = "Email liên hệ đã được sử dụng. Vui lòng chọn email khác.";
            } else if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("ClubName")) {
                errorMsg = "Tên câu lạc bộ '" + clubName + "' đã được sử dụng. Vui lòng chọn tên khác.";
            }
            request.setAttribute("error", errorMsg);
        }

        request.setAttribute("hasPermission", true);
        request.setAttribute("approvedClubName", clubName);
        request.setAttribute("approvedCategory", category);
        request.setAttribute("departments", clubDAO.getAllDepartments());
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
    }

    private void handleUpdateClub(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int clubID;
        try {
            clubID = Integer.parseInt(request.getParameter("clubID"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid club ID");
            return;
        }

        UserClub userClub = userClubDAO.getUserClub(user.getUserID(), clubID);
        if (userClub == null || userClub.getRoleID() != 1) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa câu lạc bộ này.");
            request.setAttribute("isPresident", false);
            request.setAttribute("club", clubDAO.getClubById(clubID));
            request.setAttribute("departments", clubDAO.getAllDepartments());
            request.setAttribute("clubDepartmentIDs", clubDAO.getClubDepartmentIDs(clubID));
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        String clubName = request.getParameter("clubName");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String contactPhone = request.getParameter("contactPhone");
        String contactGmail = request.getParameter("contactGmail");
        String contactURL = request.getParameter("contactURL");
        String establishedDateStr = request.getParameter("establishedDate");
        Part filePart = request.getPart("clubImg");
        String[] departmentIDs = request.getParameterValues("departmentIDs");

        System.out.println("Received update form data:");
        System.out.println("clubID: " + clubID);
        System.out.println("clubName: " + clubName);
        System.out.println("category: " + category);
        System.out.println("contactGmail: " + contactGmail);
        System.out.println("description: " + description);
        System.out.println("contactPhone: " + contactPhone);
        System.out.println("contactURL: " + contactURL);
        System.out.println("establishedDate: " + establishedDateStr);
        System.out.println("filePart: " + (filePart != null ? filePart.getName() + ", size: " + filePart.getSize() + ", type: " + filePart.getContentType() : "null"));
        System.out.println("departmentIDs: " + (departmentIDs != null ? Arrays.toString(departmentIDs) : "null"));

        List<String> errors = new ArrayList<>();
        if (clubName == null || clubName.trim().isEmpty()) {
            errors.add("Tên câu lạc bộ không được để trống.");
        } else if (clubDAO.isClubNameTaken(clubName, clubID)) {
            errors.add("Tên câu lạc bộ '" + clubName + "' đã được sử dụng. Vui lòng chọn tên khác.");
        }
        if (contactGmail == null || contactGmail.trim().isEmpty()) {
            errors.add("Email liên hệ không được để trống.");
        }
        if (category == null || category.trim().isEmpty()) {
            errors.add("Vui lòng chọn danh mục câu lạc bộ.");
        } else if (!VALID_CATEGORIES.contains(category)) {
            errors.add("Danh mục không hợp lệ. Vui lòng chọn Thể Thao, Học Thuật hoặc Phong Trào.");
        }
        Date establishedDate = null;
        if (establishedDateStr != null && !establishedDateStr.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                establishedDate = new Date(sdf.parse(establishedDateStr).getTime());
            } catch (Exception e) {
                errors.add("Ngày thành lập không hợp lệ.");
            }
        }

        Clubs club = clubDAO.getClubById(clubID);
        String relativePath = club.getClubImg();
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String contentType = filePart.getContentType().toLowerCase();

            if (!contentType.startsWith("image/") || 
                !(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                  fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".webp"))) {
                errors.add("Chỉ được upload file ảnh (.jpg, .png, .gif, .webp)");
            } else {
                String ext = fileName.substring(fileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + ext;

                String folder = UPLOAD_DIR;
                String buildPath = getServletContext().getRealPath("/");
                String webPath = buildPath.replace("build" + File.separator + "web", "web") + folder;
                String buildImgPath = buildPath + folder;

                File webDir = new File(webPath);
                if (!webDir.exists()) {
                    webDir.mkdirs();
                }
                File buildDir = new File(buildImgPath);
                if (!buildDir.exists()) {
                    buildDir.mkdirs();
                }

                String webUploadPath = webPath + File.separator + newFileName;
                try {
                    filePart.write(webUploadPath);
                    String buildUploadPath = buildImgPath + File.separator + newFileName;
                    Files.copy(new File(webUploadPath).toPath(), new File(buildUploadPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    relativePath = folder + "/" + newFileName;
                    System.out.println("Image saved successfully: " + relativePath);
                } catch (IOException e) {
                    errors.add("Lỗi khi upload ảnh: " + e.getMessage());
                }
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("error", String.join("<br>", errors));
            request.setAttribute("isPresident", true);
            request.setAttribute("club", clubDAO.getClubById(clubID));
            request.setAttribute("departments", clubDAO.getAllDepartments());
            request.setAttribute("clubDepartmentIDs", clubDAO.getClubDepartmentIDs(clubID));
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        club.setClubName(clubName);
        club.setDescription(description);
        club.setCategory(category);
        club.setClubImg(relativePath);
        club.setContactPhone(contactPhone);
        club.setContactGmail(contactGmail);
        club.setContactURL(contactURL);
        club.setEstablishedDate(establishedDate);

        List<Integer> newDepartmentIDList = departmentIDs != null ?
                Arrays.stream(departmentIDs)
                      .filter(id -> id != null && !id.trim().isEmpty())
                      .map(Integer::parseInt)
                      .collect(Collectors.toList()) :
                new ArrayList<>();
        if (!newDepartmentIDList.contains(3)) {
            newDepartmentIDList.add(3);
        }
        System.out.println("Processed newDepartmentIDList: " + newDepartmentIDList);

        try {
            boolean updated = clubDAO.updateClub(club, newDepartmentIDList);
            if (updated) {
                Clubs updatedClub = clubDAO.getClubById(clubID);
                session.setAttribute("currentClub_" + clubID, updatedClub);
                System.out.println("Updated club saved to session with ID: " + clubID + ", ClubImg: " + updatedClub.getClubImg());
                request.setAttribute("club", updatedClub);
                request.setAttribute("message", "Cập nhật câu lạc bộ thành công.");
                response.sendRedirect(request.getContextPath() + "/club-detail?id=" + clubID + "&t=" + System.currentTimeMillis());
                return;
            } else {
                request.setAttribute("error", "Cập nhật câu lạc bộ thất bại. Vui lòng kiểm tra email liên hệ (có thể đã tồn tại).");
            }
        } catch (SQLException e) {
            String errorMsg = "Lỗi khi cập nhật câu lạc bộ: " + e.getMessage();
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("ContactGmail")) {
                errorMsg = "Email liên hệ đã được sử dụng. Vui lòng chọn email khác.";
            } else if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("ClubName")) {
                errorMsg = "Tên câu lạc bộ '" + clubName + "' đã được sử dụng. Vui lòng chọn tên khác.";
            }
            request.setAttribute("error", errorMsg);
        }

        request.setAttribute("isPresident", true);
        request.setAttribute("club", club);
        request.setAttribute("departments", clubDAO.getAllDepartments());
        request.setAttribute("clubDepartmentIDs", clubDAO.getClubDepartmentIDs(clubID));
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
    }

    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                String fileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                return System.currentTimeMillis() + "_" + fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
            }
        }
        return "";
    }
}
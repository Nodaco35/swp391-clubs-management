package controller;

import dal.ApprovalHistoryDAO;
import dal.ClubCategoryDAO;
import dal.ClubDAO;
import dal.UserClubDAO;
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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.ClubCategory;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024 * 1024 * 5, // 5MB
        maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class CreateClubServlet extends HttpServlet {

    private ClubDAO clubDAO;
    private UserClubDAO userClubDAO;
    private CreatedClubApplicationsDAO applicationDAO;
    private ClubCategoryDAO clubCategoryDAO;
    private static final String UPLOAD_DIR = "img";

    @Override
    public void init() throws ServletException {
        clubDAO = new ClubDAO();
        userClubDAO = new UserClubDAO();
        applicationDAO = new CreatedClubApplicationsDAO();
        clubCategoryDAO = new ClubCategoryDAO();
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
        } else if (action != null && action.equals("editRejectClub")) {
            handleRejectEditClub(request, response);
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
        List<ClubCategory> categories = clubCategoryDAO.getAllCategories();
        request.setAttribute("categories", categories);
        request.setAttribute("isEdit", false);
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
            request.setAttribute("categories", clubCategoryDAO.getAllCategories());
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        request.setAttribute("club", club);
        request.setAttribute("categories", clubCategoryDAO.getAllCategories());
        request.setAttribute("isEdit", true);
        request.setAttribute("isPresident", true);
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

        String clubName = request.getParameter("clubName");
        String description = request.getParameter("description");
        String contactPhone = request.getParameter("contactPhone");
        String contactGmail = request.getParameter("contactGmail");
        String contactURL = request.getParameter("contactURL");
        String establishedDateStr = request.getParameter("establishedDate");
        Part filePart = request.getPart("clubImg");
        String categoryParam = request.getParameter("category");

        int categoryID;
        try {
            categoryID = Integer.parseInt(categoryParam);
        } catch (NumberFormatException e) {
            categoryID = 0;
        }

        List<String> errors = new ArrayList<>();
        if (clubName == null || clubName.trim().isEmpty()) {
            errors.add("Tên câu lạc bộ không được để trống.");
        } else if (clubDAO.isClubNameTaken(clubName, 0)) {
            errors.add("Tên câu lạc bộ '" + clubName + "' đã được sử dụng. Vui lòng chọn tên khác.");
        }
        if (categoryID <= 0) {
            errors.add("Danh mục không hợp lệ.");
        } else {
            boolean validCategory = false;
            for (ClubCategory category : clubCategoryDAO.getAllCategories()) {
                if (category.getCategoryID() == categoryID) {
                    validCategory = true;
                    break;
                }
            }
            if (!validCategory) {
                errors.add("Danh mục không hợp lệ.");
            }
        }
        if (contactGmail == null || contactGmail.trim().isEmpty()) {
            errors.add("Email liên hệ không được để trống.");
        }
        if (contactPhone != null && !contactPhone.trim().isEmpty() && !contactPhone.matches("\\d{10}")) {
            errors.add("Số điện thoại phải là 10 chữ số và không chứa chữ cái hoặc ký tự đặc biệt.");
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

            if (!contentType.startsWith("image/")
                    || !(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                    || fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".webp"))) {
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
                } catch (IOException e) {
                    errors.add("Lỗi khi upload ảnh: " + e.getMessage());
                }
            }
        } else {
            errors.add("Vui lòng chọn một hình ảnh.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("error", String.join("<br>", errors));
            request.setAttribute("categories", clubCategoryDAO.getAllCategories());
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        Clubs club = new Clubs();
        club.setClubName(clubName);
        club.setDescription(description);
        club.setCategoryID(categoryID);
        club.setClubImg(relativePath);
        club.setContactPhone(contactPhone);
        club.setContactGmail(contactGmail);
        club.setContactURL(contactURL);
        club.setEstablishedDate(establishedDate);
        club.setClubStatus(false);
        club.setIsRecruiting(false);

        List<Integer> departmentIDList = new ArrayList<>();
        departmentIDList.add(3); // Automatically include department ID 3

        try {
            int newClubID = clubDAO.createClub(club);
            if (newClubID > 0) {
                int clubDepartmentID = userClubDAO.getClubDepartmentIdByClubAndDepartment(newClubID, 3);

                UserClub newUserClub = new UserClub();
                newUserClub.setUserID(user.getUserID());
                newUserClub.setClubID(newClubID);
                newUserClub.setRoleID(1);
                newUserClub.setClubDepartmentID(clubDepartmentID);
                newUserClub.setJoinDate(establishedDate);
                newUserClub.setIsActive(true);
                userClubDAO.addUserClub(newUserClub);
                Clubs createdClub = clubDAO.getClubById(newClubID);
                session.setAttribute("currentClub_" + newClubID, createdClub);
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

        request.setAttribute("categories", clubCategoryDAO.getAllCategories());
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
            request.setAttribute("categories", clubCategoryDAO.getAllCategories());
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        String clubName = request.getParameter("clubName");
        String description = request.getParameter("description");
        String categoryParam = request.getParameter("category");
        String contactPhone = request.getParameter("contactPhone");
        String contactGmail = request.getParameter("contactGmail");
        String contactURL = request.getParameter("contactURL");
        String establishedDateStr = request.getParameter("establishedDate");
        Part filePart = request.getPart("clubImg");
        String updateRequestNote = request.getParameter("updateRequestNote");
        try {
            int categoryID;
            try {
                categoryID = Integer.parseInt(categoryParam);
            } catch (NumberFormatException e) {
                categoryID = 0;
            }

            List<String> errors = new ArrayList<>();
            if (clubName == null || clubName.trim().isEmpty()) {
                errors.add("Tên câu lạc bộ không được để trống.");
            }
//        } else if (clubDAO.isClubNameTaken(clubName, clubID)) {
//            errors.add("Tên câu lạc bộ '" + clubName + "' đã được sử dụng. Vui lòng chọn tên khác.");
//        }
            if (contactGmail == null || contactGmail.trim().isEmpty()) {
                errors.add("Email liên hệ không được để trống.");
            }
            if (categoryID <= 0) {
                errors.add("Vui lòng chọn danh mục câu lạc bộ.");
            } else {
                boolean validCategory = false;
                for (ClubCategory category : clubCategoryDAO.getAllCategories()) {
                    if (category.getCategoryID() == categoryID) {
                        validCategory = true;
                        break;
                    }
                }
                if (!validCategory) {
                    errors.add("Danh mục không hợp lệ.");
                }
            }
            if (contactPhone != null && !contactPhone.trim().isEmpty() && !contactPhone.matches("\\d{10}")) {
                errors.add("Số điện thoại phải là 10 chữ số và không chứa chữ cái hoặc ký tự đặc biệt.");
            }
            if (updateRequestNote == null || updateRequestNote.trim().isEmpty()) {
                errors.add("Ghi chú yêu cầu cập nhật không được để trống.");
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

            Clubs originalClub = clubDAO.getClubById(clubID);
            String clubRequestStatus = originalClub.getClubRequestStatus();
            String currentRequestType = originalClub.getCurrentRequestType();

            String relativePath = originalClub.getClubImg();
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String contentType = filePart.getContentType().toLowerCase();

                if (!contentType.startsWith("image/")
                        || !(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                        || fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".webp"))) {
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
                    } catch (IOException e) {
                        errors.add("Lỗi khi upload ảnh: " + e.getMessage());
                    }
                }
            }

            if (!errors.isEmpty()) {
                request.setAttribute("error", String.join("<br>", errors));
                request.setAttribute("isPresident", true);
                request.setAttribute("club", clubDAO.getClubById(clubID));
                request.setAttribute("categories", clubCategoryDAO.getAllCategories());
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
                return;
            }

            Clubs newClub = new Clubs();

            newClub.setClubName(clubName);
            newClub.setDescription(description);
            newClub.setCategoryID(categoryID);
            newClub.setClubImg(relativePath);
            newClub.setContactPhone(contactPhone);
            newClub.setContactGmail(contactGmail);
            newClub.setContactURL(contactURL);
            newClub.setEstablishedDate(establishedDate);
            newClub.setUpdateRequestNote(updateRequestNote);
            newClub.setClubRequestStatus(clubRequestStatus);
            newClub.setCurrentRequestType(currentRequestType);

            List<Integer> newDepartmentIDList = new ArrayList<>();
            newDepartmentIDList.add(3); // Automatically include department ID 3

            if (currentRequestType.equals("Create") && clubRequestStatus.equals("Rejected")) {
                newClub.setClubID(clubID);
                boolean updateForm = clubDAO.updateClub(newClub);
                if (updateForm) {
                    session.setAttribute("message", "Yêu cầu cập nhật đơn tạo câu lạc bộ đã được gửi và đang chờ duyệt.");
                    ApprovalHistoryDAO ap = new ApprovalHistoryDAO();
                    ap.insertApprovalRecord(clubID, "Pending", "Nộp lại đơn xét duyệt", "Create");
                    response.sendRedirect(request.getContextPath() + "/club-detail?id=" + clubID);
                    return;
                } else {
                    request.setAttribute("error", "Gửi yêu cầu cập nhật thất bại. Vui lòng kiểm tra email liên hệ.");
                }

            } else {
                // tạo club mới, ParentClubID(mới) = clubId (cũ)  clubStatus = 0
                int newClubID = clubDAO.requestUpdateClub(newClub, clubID);
                //updateclub cũ CurrentRequestType = 'Update' ClubRequestStatus = 'Pending'
                if (newClubID > 0) {
                    session.setAttribute("message", "Yêu cầu cập nhật câu lạc bộ đã được gửi và đang chờ duyệt.");
                    response.sendRedirect(request.getContextPath() + "/club-detail?id=" + clubID);
                    return;
                } else {
                    request.setAttribute("error", "Gửi yêu cầu cập nhật thất bại. Vui lòng kiểm tra email liên hệ.");
                }
            }

        } catch (SQLException e) {
            String errorMsg = "Lỗi khi gửi yêu cầu cập nhật: " + e.getMessage();
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("ContactGmail")) {
                errorMsg = "Email liên hệ đã được sử dụng. Vui lòng chọn email khác.";
            } else if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("ClubName")) {
                errorMsg = "Tên câu lạc bộ '" + clubName + "' đã được sử dụng. Vui lòng chọn tên khác.";
            }
            request.setAttribute("error", errorMsg);
        }
        Clubs originalClub = clubDAO.getClubById(clubID);
        request.setAttribute("isPresident", true);
        request.setAttribute("club", originalClub);
        request.setAttribute("categories", clubCategoryDAO.getAllCategories());
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
    }

    private void handleRejectEditClub(HttpServletRequest request, HttpServletResponse response)
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
        String clubRequestStatus = club.getClubRequestStatus();
        String rejectReason = club.getLastRejectReason();

        UserClub userClub = userClubDAO.getUserClub(user.getUserID(), clubID);
        if (userClub == null || userClub.getRoleID() != 1) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa câu lạc bộ này.");
            request.setAttribute("isPresident", false);
            request.setAttribute("club", club);
            request.setAttribute("categories", clubCategoryDAO.getAllCategories());
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
            return;
        }

        request.setAttribute("club", club);
        request.setAttribute("clubRequestStatus", clubRequestStatus);
        request.setAttribute("rejectReason", rejectReason);
        request.setAttribute("categories", clubCategoryDAO.getAllCategories());
        request.setAttribute("isEditRejectClub", true);
        request.setAttribute("isPresident", true);
        request.getRequestDispatcher("/view/clubs-page/create-club.jsp").forward(request, response);
    }
}
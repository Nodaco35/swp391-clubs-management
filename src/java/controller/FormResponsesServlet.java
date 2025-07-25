package controller;

import dal.UserClubDAO;
import dal.FormResponseDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.UserClub;
import models.ClubApplicationExtended;
import com.google.gson.Gson;

/**
 *
 * @author Vinh
 */
public class FormResponsesServlet extends HttpServlet {

    private FormResponseDAO formResponseDAO;
    private UserClubDAO userClubDAO;
    private static final Logger LOGGER = Logger.getLogger(FormResponsesServlet.class.getName());

    @Override
    public void init() {
        formResponseDAO = new FormResponseDAO();
        userClubDAO = new UserClubDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            LOGGER.info("FormResponsesServlet.doGet - Starting");
            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("userID");
            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String formIdParam = request.getParameter("formId");
            Integer formId = null;

            if (formIdParam != null && !formIdParam.isEmpty()) {
                try {
                    formId = Integer.parseInt(formIdParam);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("ID Form không hợp lệ.", StandardCharsets.UTF_8.name()));
                    return;
                }
            }

            // Kiểm tra nếu formId là null
            if (formId == null) {
                response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_form&message="
                        + URLEncoder.encode("Vui lòng chọn form để xem phản hồi.", StandardCharsets.UTF_8.name()));
                return;
            }

            String clubIdParam = request.getParameter("clubId");
            Integer clubId = null;

            if (clubIdParam != null && !clubIdParam.isEmpty()) {
                try {
                    clubId = Integer.parseInt(clubIdParam);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("ID CLB không hợp lệ.", StandardCharsets.UTF_8.name()));
                    return;
                }
            }

            // Kiểm tra nếu clubId là null thì redirect về my-club
            if (clubId == null) {
                response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_club&message="
                        + URLEncoder.encode("Vui lòng chọn câu lạc bộ để quản lý form.", StandardCharsets.UTF_8.name()));
                return;
            }

            // Kiểm tra quyền truy cập trong club cụ thể (chỉ cho roleId 1-3)
            UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
            if (userClub == null) {
                response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền quản lý form trong CLB này.", StandardCharsets.UTF_8.name()));
                return;
            }

            // Xác định loại form
            String formTypeParam = request.getParameter("formType");
            String formType = "club";
            if (formTypeParam != null && formTypeParam.toLowerCase().equals("event")) {
                formType = "event";
            }
            //Kiểm tính hợp lệ của form
            if (!formResponseDAO.isFormValid(formId, clubId, formType)) {
                response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_form&message="
                        + URLEncoder.encode("Form không hợp lệ hoặc không thuộc câu lạc bộ này.", StandardCharsets.UTF_8.name()));
                return;
            }
            // Lấy danh sách đơn đăng ký
            try {
                List<ClubApplicationExtended> applications = formResponseDAO.getApplicationsByFormAndClub(formId, clubId);
                // Chuyển đổi dữ liệu thành JSON để JavaScript có thể sử dụng
                Gson gson = new Gson();
                String applicationsJson = gson.toJson(applications);

                request.setAttribute("applicationsJson", applicationsJson);

                // Thêm danh sách applications để JSP có thể sử dụng
                request.setAttribute("applications", applications);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách đơn đăng ký", e);
                request.setAttribute("applicationsJson", "[]");
            }

            // Thêm thông tin formId, clubId và formType để sử dụng trong trang formResponse.jsp
            request.setAttribute("formId", formId);
            request.setAttribute("clubId", clubId);
            request.setAttribute("formType", formType);
            request.getRequestDispatcher("/view/student/chairman/formResponse.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi trong doGet", e);
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi lấy dữ liệu. Vui lòng thử lại sau.");
            request.setAttribute("applicationsJson", "[]"); // Empty array as fallback
            request.getRequestDispatcher("/view/student/chairman/formResponse.jsp")
                    .forward(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            // Kiểm tra đăng nhập
            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("userID");
            if (userId == null) {
                out.print(gson.toJson(new ApiResponse(false, "Bạn cần đăng nhập để thực hiện thao tác này.")));
                return;
            }
            // Xác định hành động từ request parameter
            String action = request.getParameter("action");

            // Nếu action là checkResponses, xử lý việc kiểm tra phản hồi
            if ("checkResponses".equals(action)) {
                // Lấy clubId từ request parameter
                String clubIdParam = request.getParameter("clubId");
                Integer clubId = null;

                if (clubIdParam != null && !clubIdParam.isEmpty()) {
                    try {
                        clubId = Integer.parseInt(clubIdParam);
                    } catch (NumberFormatException e) {
                        out.print(gson.toJson(new ApiResponse(false, "ID CLB không hợp lệ.")));
                        return;
                    }
                }

                // Kiểm tra nếu clubId là null
                if (clubId == null) {
                    out.print(gson.toJson(new ApiResponse(false, "Vui lòng chọn câu lạc bộ.")));
                    return;
                }

                // Kiểm tra quyền truy cập trong club cụ thể
                UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
                if (userClub == null) {
                    out.print(gson.toJson(new ApiResponse(false, "Bạn không có quyền quản lý form trong CLB này.")));
                    return;
                }

                try {
                    // Lấy danh sách formId có phản hồi
                    List<Integer> formIdsWithResponses = formResponseDAO.getFormIdsWithResponses(clubId);

                    // Gửi kết quả về client
                    out.print(gson.toJson(formIdsWithResponses));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra phản hồi form", e);
                    out.print(gson.toJson(new ApiResponse(false, "Đã xảy ra lỗi khi kiểm tra phản hồi form: " + e.getMessage())));
                }

                return;
            }

            // Lấy thông tin từ request
            String responseIdStr = request.getParameter("responseId");
            String clubIdStr = request.getParameter("clubId");

            LOGGER.info("FormResponsesServlet.doPost - Action: " + action + ", ResponseID: " + responseIdStr);

            if (action == null || responseIdStr == null || clubIdStr == null) {
                out.print(gson.toJson(new ApiResponse(false, "Thiếu tham số cần thiết.")));
                return;
            }

            // Parse IDs
            int responseId;
            int clubId;

            try {
                responseId = Integer.parseInt(responseIdStr);
                clubId = Integer.parseInt(clubIdStr);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Số ID không hợp lệ", e);
                out.print(gson.toJson(new ApiResponse(false, "ID không hợp lệ.")));
                return;
            }

            // Kiểm tra quyền của user trong club
            UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
            if (userClub == null) {
                out.print(gson.toJson(new ApiResponse(false, "Bạn không có quyền quản lý trong câu lạc bộ này.")));
                return;
            }

            boolean success = false;
            String message = "";
            String newStatus = "";

            // Thực hiện hành động dựa trên tham số action
            if ("approve".equals(action)) {
                // Lấy thông tin đơn hiện tại
                ClubApplicationExtended application = formResponseDAO.getApplicationById(responseId);
                if (application == null) {
                    out.print(gson.toJson(new ApiResponse(false, "Không tìm thấy đơn đăng ký.")));
                    return;
                }

                // Xử lý duyệt đơn
                success = formResponseDAO.approveApplication(responseId);

                // Lấy trạng thái mới sau khi duyệt
                if (success) {
                    application = formResponseDAO.getApplicationById(responseId);
                    if (application != null) {
                        newStatus = application.getResponseStatus().toLowerCase();

                        // Hiển thị thông báo phù hợp với trạng thái mới
                        switch (newStatus) {
                            case "approved":
                                message = "event".equalsIgnoreCase(application.getFormType())
                                        ? "Đã duyệt đăng ký sự kiện thành công"
                                        : "Đã nâng cấp thành thành viên chính thức";
                                break;
                            case "candidate":
                                message = "Đã duyệt thành ứng viên";
                                break;
                            case "collaborator":
                                message = "Đã nâng cấp thành cộng tác viên";
                                break;
                            default:
                                message = "Đã cập nhật trạng thái thành công";
                        }
                    } else {
                        message = "Đã duyệt đơn thành công";
                    }
                } else {
                    message = "Không thể duyệt đơn. Vui lòng thử lại sau.";
                }
            } else if ("reject".equals(action)) {
                // Xử lý từ chối đơn
                success = formResponseDAO.rejectApplication(responseId);
                message = success ? "Đã từ chối đơn đăng ký" : "Không thể từ chối đơn. Vui lòng thử lại sau.";
                newStatus = "rejected";
            } else if ("saveReview".equals(action)) {
                // Lấy nội dung đánh giá từ request
                String reviewNote = request.getParameter("reviewNote");
                if (reviewNote == null) {
                    reviewNote = "";
                }

                // Lưu đánh giá vào database
                try {
                    success = formResponseDAO.saveReviewNote(responseId, reviewNote);
                    message = success ? "Đã lưu đánh giá thành công" : "Không thể lưu đánh giá. Vui lòng thử lại sau.";
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi lưu đánh giá", e);
                    success = false;
                    message = "Đã xảy ra lỗi: " + e.getMessage();
                }
            } else {
                out.print(gson.toJson(new ApiResponse(false, "Hành động không hợp lệ.")));
                return;
            }

            // Trả về kết quả
            ApiResponse response1 = new ApiResponse(success, message);
            response1.setStatus(newStatus);
            out.print(gson.toJson(response1));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi xử lý trong doPost", e);
            response.setContentType("application/json");
            response.setStatus(200); // Use 200 instead of 500 to ensure the response is processed by fetch
            out.print(gson.toJson(new ApiResponse(false, "Đã xảy ra lỗi: " + e.getMessage())));
        }
    }

    // Lớp hỗ trợ để trả về API response dạng JSON
    private static class ApiResponse {

        private boolean success;
        private String message;
        private String status;
        private Object data;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    /**
     * Trả về mô tả ngắn về servlet.
     *
     * @return một String chứa mô tả servlet
     */
    @Override
    public String getServletInfo() {
        return "Xử lý phản hồi biểu mẫu đơn đăng ký";
    }
}

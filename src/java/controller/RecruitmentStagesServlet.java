package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;

import models.RecruitmentStage;
import models.Users;
import service.RecruitmentService;

/**
 * API Servlet để lấy thông tin các giai đoạn của một hoạt động tuyển quân
 */
@WebServlet(name = "RecruitmentStagesServlet", urlPatterns = {"/recruitment/stages"})
public class RecruitmentStagesServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecruitmentStagesServlet.class.getName());
    private final RecruitmentService recruitmentService = new RecruitmentService();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo charset UTF-8 cho response
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Người dùng chưa đăng nhập");
            out.print(jsonResponse.toString());
            return;
        }
        
        try {
            // Lấy ID hoạt động tuyển quân từ parameter
            String recruitmentIdParam = request.getParameter("recruitmentId");
            if (recruitmentIdParam == null || recruitmentIdParam.isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Thiếu ID hoạt động tuyển quân");
                out.print(jsonResponse.toString());
                return;
            }
            
            int recruitmentId = Integer.parseInt(recruitmentIdParam);
            
            // Lấy danh sách các giai đoạn
            List<RecruitmentStage> stages = recruitmentService.getStagesByCampaign(recruitmentId);
            
            // Trả về danh sách dưới dạng JSON
            jsonResponse.addProperty("success", true);
            jsonResponse.add("stages", gson.toJsonTree(stages));
            
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "ID hoạt động không hợp lệ: {0}", e.getMessage());
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "ID hoạt động không hợp lệ");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu giai đoạn: {0}", e.getMessage());
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Lỗi khi lấy dữ liệu giai đoạn: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
    }
}

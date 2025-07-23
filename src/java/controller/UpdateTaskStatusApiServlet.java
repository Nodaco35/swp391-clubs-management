package controller;

import com.google.gson.Gson;
import dal.TaskDAO;
import models.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/update-task-status")
@MultipartConfig
public class UpdateTaskStatusApiServlet extends HttpServlet {
    
    private TaskDAO taskDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        taskDAO = new TaskDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        Map<String, Object> result = new HashMap<>();
        
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "Bạn cần đăng nhập để cập nhật trạng thái");
            response.getWriter().write(gson.toJson(result));
            return;
        }
        
        try {
            String taskIdStr = request.getParameter("taskId");
            String status = request.getParameter("status");
            
            if (taskIdStr == null || taskIdStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Thiếu thông tin ID nhiệm vụ");
                response.getWriter().write(gson.toJson(result));
                return;
            }
            
            if (status == null || status.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Vui lòng chọn trạng thái");
                response.getWriter().write(gson.toJson(result));
                return;
            }
            
            int taskId = Integer.parseInt(taskIdStr);
            
            // Update task status only
            boolean success = taskDAO.updateTaskStatus(taskId, status);
            
            if (success) {
                result.put("success", true);
                result.put("message", "Cập nhật trạng thái thành công");
            } else {
                result.put("success", false);
                result.put("message", "Không thể cập nhật trạng thái nhiệm vụ");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Dữ liệu số không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        response.getWriter().write(gson.toJson(result));
    }
}

package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dal.TaskDAO;
import models.Tasks;
import models.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/task-detail")
public class TaskDetailApiServlet extends HttpServlet {
    
    private TaskDAO taskDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        taskDAO = new TaskDAO();
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        Map<String, Object> result = new HashMap<>();
        
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "Bạn cần đăng nhập để xem chi tiết nhiệm vụ");
            response.getWriter().write(gson.toJson(result));
            return;
        }
        
        try {
            String taskIdStr = request.getParameter("taskId");
            if (taskIdStr == null || taskIdStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Thiếu thông tin ID nhiệm vụ");
                response.getWriter().write(gson.toJson(result));
                return;
            }
            
            int taskId = Integer.parseInt(taskIdStr);
            
            // Get task detail
            Tasks task = taskDAO.getTasksByID(taskId);
            
            if (task == null) {
                result.put("success", false);
                result.put("message", "Không tìm thấy nhiệm vụ");
            } else {
                result.put("success", true);
                result.put("task", task);
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "ID nhiệm vụ không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        response.getWriter().write(gson.toJson(result));
    }
}

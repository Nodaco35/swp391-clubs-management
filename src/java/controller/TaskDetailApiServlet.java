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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

@WebServlet("/task-detail")
public class TaskDetailApiServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(TaskDetailApiServlet.class.getName());
    private TaskDAO taskDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        taskDAO = new TaskDAO();
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
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
            
            LOGGER.log(Level.INFO, "Fetching task details for ID: {0}", taskId);
            
            // Get task detail
            Tasks task = taskDAO.getTasksByID(taskId);
            
            if (task == null) {
                LOGGER.log(Level.WARNING, "Task not found for ID: {0}", taskId);
                result.put("success", false);
                result.put("message", "Không tìm thấy nhiệm vụ");
            } else {
                LOGGER.log(Level.INFO, "Task found: {0}, Title: {1}", new Object[]{taskId, task.getTitle()});
                result.put("success", true);
                result.put("task", task);
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "ID nhiệm vụ không hợp lệ");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing task detail request", e);
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Có lỗi xảy ra: " + e.getMessage());
            result.put("error_details", e.getClass().getSimpleName());
        }
        
        String jsonResponse = gson.toJson(result);
        LOGGER.log(Level.INFO, "Response JSON length: {0}", jsonResponse.length());
        response.getWriter().write(jsonResponse);
    }
}

package controller;

import dal.DepartmentDashboardDAO;
import models.DepartmentDashboard;
import models.Users;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DepartmentDashboardServlet extends HttpServlet {
    
    private DepartmentDashboardDAO dashboardDAO;
    
    @Override
    public void init() throws ServletException {
        dashboardDAO = new DepartmentDashboardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");        // Kiểm tra đăng nhập
        if (currentUser == null) {
            //Tạm thời comment để test
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Kiểm tra quyền trưởng ban
            if (!dashboardDAO.isDepartmentLeader(currentUser.getUserID())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
                return;
            }
      
            int clubID = Integer.parseInt(request.getParameter("clubID"));
            session.setAttribute("clubID",clubID);
            //Fhuc: Lay ClubDepartmentID -> Set session
            int clubdepartmentId = dashboardDAO.findClubDepartmentId(clubID, currentUser.getUserID());
            session.setAttribute("clubDepartmentId", clubdepartmentId);
            
            // Lấy dữ liệu dashboard
            DepartmentDashboard dashboard = dashboardDAO.getCompleteDashboard(currentUser.getUserID());
              if (dashboard == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy thông tin ban");
                return;
            }
              // Đưa dữ liệu vào request
            request.setAttribute("dashboard", dashboard);
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("departmentName", dashboard.getDepartmentName());
            
           
            //mới đức
            boolean accessFinancial = false;
            if (dashboardDAO.isDepartmentLeaderIndoingoai(currentUser.getUserID(), clubID)) {
                accessFinancial = true;
            }
            session.setAttribute("isAccess", accessFinancial);
            // Forward đến JSP
            request.getRequestDispatcher("view/student/department-leader/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

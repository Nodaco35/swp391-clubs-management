package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import models.DepartmentMeeting;
import models.DepartmentMeetingParticipant;
import models.DepartmentMember;
import models.Users;
import service.DepartmentMeetingService;
import service.DepartmentMemberService;

/**
 * Controller for managing department meetings
 */

public class DepartmentMeetingController extends HttpServlet {
    
    private final DepartmentMeetingService departmentMeetingService;
    private final DepartmentMemberService departmentMemberService;
    
    public DepartmentMeetingController() {
        this.departmentMeetingService = new DepartmentMeetingService();
        this.departmentMemberService = new DepartmentMemberService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy clubID từ session hoặc parameter
        // Trong thực tế, bạn sẽ cần có cơ chế để lấy thông tin clubID và departmentID của user hiện tại
        int clubID = 0;
        int departmentID = 0;
        
        try {
            if (request.getParameter("clubID") != null) {
                clubID = Integer.parseInt(request.getParameter("clubID"));
                session.setAttribute("clubID", clubID);
            } else if (session.getAttribute("clubID") != null) {
                clubID = (Integer) session.getAttribute("clubID");
            }
            
            if (request.getParameter("departmentID") != null) {
                departmentID = Integer.parseInt(request.getParameter("departmentID"));
                session.setAttribute("departmentID", departmentID);
            } else if (session.getAttribute("departmentID") != null) {
                departmentID = (Integer) session.getAttribute("departmentID");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/user-clubs");
            return;
        }
        
        if (clubID == 0 || departmentID == 0) {
            response.sendRedirect(request.getContextPath() + "/user-clubs");
            return;
        }
        
        switch (action) {
            case "list":
                listMeetings(request, response, departmentID);
                break;
            case "view":
                viewMeetingDetail(request, response, departmentID);
                break;
            case "add":
                showAddForm(request, response, departmentID);
                break;
            case "edit":
                showEditForm(request, response, departmentID);
                break;
            case "delete":
                deleteMeeting(request, response, departmentID);
                break;
            default:
                listMeetings(request, response, departmentID);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        HttpSession session = request.getSession();
        int departmentID = 0;
        
        if (session.getAttribute("departmentID") != null) {
            departmentID = (Integer) session.getAttribute("departmentID");
        }
        
        switch (action) {
            case "create":
                createMeeting(request, response, departmentID);
                break;
            case "update":
                updateMeeting(request, response, departmentID);
                break;
            case "addParticipant":
                addParticipant(request, response);
                break;
            case "updateParticipantStatus":
                updateParticipantStatus(request, response);
                break;
            case "removeParticipant":
                removeParticipant(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
                break;
        }
    }
    
    /**
     * Liệt kê danh sách cuộc họp
     */
    private void listMeetings(HttpServletRequest request, HttpServletResponse response, int departmentID) throws ServletException, IOException {
        List<DepartmentMeeting> meetings = departmentMeetingService.getMeetingsByDepartment(departmentID);
        
        request.setAttribute("meetings", meetings);
        request.setAttribute("departmentID", departmentID);
        request.setAttribute("active", "meetings");
        
        // Xóa các thông báo khỏi session nếu có
        if (request.getSession().getAttribute("successMessage") != null) {
            request.setAttribute("successMessage", request.getSession().getAttribute("successMessage"));
            request.getSession().removeAttribute("successMessage");
        }
        
        if (request.getSession().getAttribute("errorMessage") != null) {
            request.setAttribute("errorMessage", request.getSession().getAttribute("errorMessage"));
            request.getSession().removeAttribute("errorMessage");
        }
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/meetings.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Xem chi tiết cuộc họp
     */
    private void viewMeetingDetail(HttpServletRequest request, HttpServletResponse response, int departmentID) throws ServletException, IOException {
        try {
            int meetingID = Integer.parseInt(request.getParameter("id"));
            DepartmentMeeting meeting = departmentMeetingService.getMeetingById(meetingID);
            
            if (meeting != null) {
                List<DepartmentMeetingParticipant> participants = meeting.getParticipants(); // Lấy participants từ meeting
                List<DepartmentMember> departmentMembers = departmentMemberService.getDepartmentMembers(departmentID);
                
                request.setAttribute("meeting", meeting);
                request.setAttribute("participants", participants);
                request.setAttribute("departmentMembers", departmentMembers);
                request.setAttribute("departmentID", departmentID);
                request.setAttribute("active", "meetings");
                
                // Xóa các thông báo khỏi session nếu có
                if (request.getSession().getAttribute("successMessage") != null) {
                    request.setAttribute("successMessage", request.getSession().getAttribute("successMessage"));
                    request.getSession().removeAttribute("successMessage");
                }
                
                if (request.getSession().getAttribute("errorMessage") != null) {
                    request.setAttribute("errorMessage", request.getSession().getAttribute("errorMessage"));
                    request.getSession().removeAttribute("errorMessage");
                }
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/meeting-detail.jsp");
                dispatcher.forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy cuộc họp.");
                response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID cuộc họp không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
        }
    }

    /**
     * Hiển thị form thêm cuộc họp mới
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response, int departmentID) throws ServletException, IOException {
        // Lấy danh sách thành viên trong ban để hiển thị trong dropdown
        List<DepartmentMember> departmentMembers = departmentMemberService.getDepartmentMembers(departmentID);
        
        request.setAttribute("departmentMembers", departmentMembers);
        request.setAttribute("departmentID", departmentID);
        request.setAttribute("active", "meetings");
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/add-meeting.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa cuộc họp
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, int departmentID) throws ServletException, IOException {
        try {
            int meetingID = Integer.parseInt(request.getParameter("id"));
            DepartmentMeeting meeting = departmentMeetingService.getMeetingById(meetingID);
            
            if (meeting != null) {
                List<DepartmentMeetingParticipant> participants = meeting.getParticipants(); // Lấy participants từ meeting
                List<DepartmentMember> departmentMembers = departmentMemberService.getDepartmentMembers(departmentID);
                
                request.setAttribute("meeting", meeting);
                request.setAttribute("participants", participants);
                request.setAttribute("departmentMembers", departmentMembers);
                request.setAttribute("departmentID", departmentID);
                request.setAttribute("active", "meetings");
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/edit-meeting.jsp");
                dispatcher.forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy cuộc họp.");
                response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID cuộc họp không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
        }
    }
    
    /**
     * Tạo cuộc họp mới
     */
    private void createMeeting(HttpServletRequest request, HttpServletResponse response, int departmentID) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        try {
            String urlMeeting = request.getParameter("urlMeeting");
            
            // Parse date and time
            String meetingDateTime = request.getParameter("meetingDateTime");
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date parsedDateTime = dateTimeFormat.parse(meetingDateTime);
            Timestamp startedTime = new Timestamp(parsedDateTime.getTime());
            
            // Tạo đối tượng cuộc họp
            DepartmentMeeting meeting = new DepartmentMeeting();
            meeting.setDepartmentID(departmentID);
            meeting.setUrlMeeting(urlMeeting);
            meeting.setStartedTime(startedTime);
            
            // Lưu vào database
            int meetingID = departmentMeetingService.createMeeting(meeting);
            
            // Xử lý người tham gia
            if (meetingID > 0) {
                String[] participantIDs = request.getParameterValues("participants");
                if (participantIDs != null && participantIDs.length > 0) {
                    List<Integer> userIDs = new ArrayList<>();
                    for (String id : participantIDs) {
                        userIDs.add(Integer.parseInt(id));
                    }
                    
                    // Thêm người tham gia với trạng thái Pending
                    departmentMeetingService.addMultipleParticipants(meetingID, userIDs, "Pending");
                }
                
                session.setAttribute("successMessage", "Cuộc họp đã được tạo thành công!");
                response.sendRedirect(request.getContextPath() + "/department-meeting?action=view&id=" + meetingID + "&departmentID=" + departmentID);
            } else {
                session.setAttribute("errorMessage", "Không thể tạo cuộc họp. Vui lòng thử lại!");
                response.sendRedirect(request.getContextPath() + "/department-meeting?action=add&departmentID=" + departmentID);
            }
        } catch (NumberFormatException | ParseException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại! " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=add&departmentID=" + departmentID);
        }
    }
    
    /**
     * Cập nhật thông tin cuộc họp
     */
    private void updateMeeting(HttpServletRequest request, HttpServletResponse response, int departmentID) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            int meetingID = Integer.parseInt(request.getParameter("meetingID"));
            DepartmentMeeting meeting = departmentMeetingService.getMeetingById(meetingID);
            
            if (meeting != null) {
                String urlMeeting = request.getParameter("urlMeeting");
                
                // Parse date and time
                String meetingDateTime = request.getParameter("meetingDateTime");
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date parsedDateTime = dateTimeFormat.parse(meetingDateTime);
                Timestamp startedTime = new Timestamp(parsedDateTime.getTime());
                
                meeting.setUrlMeeting(urlMeeting);
                meeting.setStartedTime(startedTime);
                
                boolean success = departmentMeetingService.updateMeeting(meeting);
                
                if (success) {
                    session.setAttribute("successMessage", "Cuộc họp đã được cập nhật thành công!");
                } else {
                    session.setAttribute("errorMessage", "Không thể cập nhật cuộc họp. Vui lòng thử lại!");
                }
                
                response.sendRedirect(request.getContextPath() + "/department-meeting?action=view&id=" + meetingID + "&departmentID=" + departmentID);
            } else {
                session.setAttribute("errorMessage", "Không tìm thấy cuộc họp.");
                response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
            }
        } catch (NumberFormatException | ParseException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
        }
    }
    
    /**
     * Xóa cuộc họp
     */
    private void deleteMeeting(HttpServletRequest request, HttpServletResponse response, int departmentID) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            int meetingID = Integer.parseInt(request.getParameter("id"));
            boolean success = departmentMeetingService.deleteMeeting(meetingID);
            
            if (success) {
                session.setAttribute("successMessage", "Cuộc họp đã được xóa thành công!");
            } else {
                session.setAttribute("errorMessage", "Không thể xóa cuộc họp. Vui lòng thử lại!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID cuộc họp không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
        }
    }
    
    /**
     * Thêm người tham gia vào cuộc họp
     */
    private void addParticipant(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int departmentID = 0;
        
        if (session.getAttribute("departmentID") != null) {
            departmentID = (Integer) session.getAttribute("departmentID");
        }
        
        try {
            int meetingID = Integer.parseInt(request.getParameter("meetingID"));
            int userID = Integer.parseInt(request.getParameter("userID"));
            
            // Kiểm tra xem người dùng đã tham gia cuộc họp chưa
            boolean isParticipant = departmentMeetingService.isUserParticipant(meetingID, userID);
            
            if (!isParticipant) {
                DepartmentMeetingParticipant participant = new DepartmentMeetingParticipant();
                participant.setMeetingID(meetingID);
                participant.setUserID(userID);
                participant.setStatus("Pending"); // Trạng thái mặc định khi thêm
                
                int participantID = departmentMeetingService.addParticipant(participant);
                
                if (participantID > 0) {
                    session.setAttribute("successMessage", "Đã thêm người tham gia thành công!");
                } else {
                    session.setAttribute("errorMessage", "Không thể thêm người tham gia. Vui lòng thử lại!");
                }
            } else {
                session.setAttribute("errorMessage", "Người dùng này đã tham gia cuộc họp!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=view&id=" + meetingID + "&departmentID=" + departmentID);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
        }
    }
    
    /**
     * Cập nhật trạng thái người tham gia
     */
    private void updateParticipantStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int departmentID = 0;
        
        if (session.getAttribute("departmentID") != null) {
            departmentID = (Integer) session.getAttribute("departmentID");
        }
        
        try {
            int participantID = Integer.parseInt(request.getParameter("participantID"));
            int meetingID = Integer.parseInt(request.getParameter("meetingID"));
            String status = request.getParameter("status");
            
            boolean success = departmentMeetingService.updateParticipantStatus(participantID, status);
            
            if (success) {
                session.setAttribute("successMessage", "Đã cập nhật trạng thái người tham gia thành công!");
            } else {
                session.setAttribute("errorMessage", "Không thể cập nhật trạng thái. Vui lòng thử lại!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=view&id=" + meetingID + "&departmentID=" + departmentID);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
        }
    }
    
    /**
     * Xóa người tham gia khỏi cuộc họp
     */
    private void removeParticipant(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int departmentID = 0;
        
        if (session.getAttribute("departmentID") != null) {
            departmentID = (Integer) session.getAttribute("departmentID");
        }
        
        try {
            int participantID = Integer.parseInt(request.getParameter("participantID"));
            int meetingID = Integer.parseInt(request.getParameter("meetingID"));
            
            boolean success = departmentMeetingService.removeParticipant(participantID);
            
            if (success) {
                session.setAttribute("successMessage", "Đã xóa người tham gia thành công!");
            } else {
                session.setAttribute("errorMessage", "Không thể xóa người tham gia. Vui lòng thử lại!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=view&id=" + meetingID + "&departmentID=" + departmentID);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-meeting?action=list&departmentID=" + departmentID);
        }
    }
}

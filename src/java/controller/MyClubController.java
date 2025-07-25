
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import models.*;

public class MyClubController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String error;
        Users user = (Users) request.getSession().getAttribute("user");
        DocumentsDAO documentsDAO = new DocumentsDAO();

        DepartmentDAO departmentDAO = new DepartmentDAO();
        if (user == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        List<UserClub> userclubs = UserClubDAO.findByUserID(user.getUserID());

        PeriodicReportDAO pr = new PeriodicReportDAO();
        List<UserClub> userMemberClubs = UserClubDAO.findMemberClubsByUserID(user.getUserID());

        for (UserClub umc : userMemberClubs) {
            if (pr.isUserActiveInTerm(umc.getUserID(), umc.getClubID())) {
                umc.setIsActivedCurrentTerm(true);
            } else {
                umc.setIsActivedCurrentTerm(false);
            }
        }

        String termNow = pr.getActiveTermID();

        if (userclubs.isEmpty()) {
            error = "Bạn chưa tham gia bất cứ câu lạc bộ nào!";
            request.setAttribute("error", error);
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        int countPendingApplication = ClubApplicationDAO.countpendingApplicationsFindByClub(user.getUserID());
        EventsDAO ev = new EventsDAO();
        int countUpcomingMeeting = ClubMeetingDAO.countByUserID(user.getUserID());
        List<Notification> recentNotifications = NotificationDAO.findRecentByUserID(user.getUserID());
        List<Events> upcomingEvents = ev.findByUCID(user.getUserID());
        List<ClubApplication> pendingApplications = ClubApplicationDAO.pendingApplicationsFindByClub(user.getUserID());
        List<ClubMeeting> clubmeetings = ClubMeetingDAO.findByUserID(user.getUserID());
        Term term = TermDAO.getActiveSemester();
        int hasPendingInvoices = FinancialDAO.getTotalInvoices(user.getUserID(), "Pending", term.getTermID());
        int countUpcomingDepartmentMeeting = DepartmentMeetingDAO.countByUID(user.getUserID());
        List<DepartmentMeeting> departmentmeetings = DepartmentMeetingDAO.findByUserID(user.getUserID());
        List<Clubs> listClubAsChairman = ClubDAO.findByUserIDAndChairman(user.getUserID());
        
        int countTodoLists = TaskDAO.countByUser(user.getUserID());
        List<Tasks> departmentTasks = TaskDAO.getAllByUser(user.getUserID());
         
        request.setAttribute("hasPendingInvoices", hasPendingInvoices);
        request.setAttribute("countUpcomingDepartmentMeeting", countUpcomingDepartmentMeeting);
        request.setAttribute("departmentmeetings", departmentmeetings);
        request.setAttribute("listClubAsChairman", listClubAsChairman);
        request.setAttribute("userclubs", userclubs);
        request.setAttribute("recentNotifications", recentNotifications);
        request.setAttribute("upcomingEvents", upcomingEvents);
        request.setAttribute("countUpcomingMeeting", countUpcomingMeeting);
        request.setAttribute("pendingApplications", pendingApplications);
        request.setAttribute("clubmeetings", clubmeetings);
        request.setAttribute("departmentTasks", departmentTasks);
        request.setAttribute("countTodoLists", countTodoLists);

        request.setAttribute("userMemberClubs", userMemberClubs);
        request.setAttribute("termNow", termNow);

        String action = request.getParameter("action");
        if ("registerActivity".equals(action)) {
            String userID = request.getParameter("userID");
            int clubID = Integer.parseInt(request.getParameter("clubID"));
            PeriodicReportDAO pd = new PeriodicReportDAO();

            PrintWriter out = response.getWriter();
            out.print(userID + " " + clubID);
            boolean success = pd.insertUserToActiveClub(userID, clubID);
            response.sendRedirect(request.getContextPath() + "/myclub");
            return;
        }
        if ("deleteActivity".equals(action)) {
            String userID = request.getParameter("userID");
            int clubID = Integer.parseInt(request.getParameter("clubID"));
            PeriodicReportDAO pd = new PeriodicReportDAO();

            PrintWriter out = response.getWriter();
            out.print(userID + " " + clubID);
            boolean success = pd.deleteUserToActiveClub(userID, clubID);
            response.sendRedirect(request.getContextPath() + "/myclub");
            return;
        }
        if ("deleteDocument".equals(action)) {
            int documentID = Integer.parseInt(request.getParameter("documentID"));
            int clubID = Integer.parseInt(request.getParameter("clubID"));
            boolean hasPermission = false;
            for (UserClub uc : userclubs) {
                if (uc.getClubID() == clubID && (uc.getRoleID() == 1 || uc.getRoleID() == 3)) {
                    hasPermission = true;
                    break;
                }
            }
            if (!hasPermission) {
                request.setAttribute("error", "Bạn không có quyền xóa tài liệu này.");
            } else {
                try {
                    documentsDAO.deleteDocument(documentID);
                    request.setAttribute("message", "Xóa tài liệu thành công!");
                } catch (SQLException e) {
                    request.setAttribute("error", "Tài liệu này đang được sử dụng, không thể xóa! ");
                }
            }
            List<Documents> documents = documentsDAO.findByClubID(clubID);
            List<Department> departments = departmentDAO.findByClubID(clubID);
            request.setAttribute("documents", documents);
            request.setAttribute("departments", departments);
            request.setAttribute("selectedClubID", clubID);
            request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        DocumentsDAO documentsDAO = new DocumentsDAO();

        DepartmentDAO departmentDAO = new DepartmentDAO();
        if (user == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        List<UserClub> userclubs = UserClubDAO.findByUserID(user.getUserID());
        String action = request.getParameter("action");

        if ("loadDocuments".equals(action)) {
            int clubID = Integer.parseInt(request.getParameter("clubID"));
            boolean isMember = false;
            for (UserClub uc : userclubs) {
                if (uc.getClubID() == clubID) {
                    isMember = true;
                    break;
                }
            }
            if (!isMember) {
                request.setAttribute("error", "Bạn không phải là thành viên của câu lạc bộ này.");
            } else {
                List<Documents> documents = documentsDAO.findByClubID(clubID);
                List<Department> departments = departmentDAO.findByClubID(clubID);
                request.setAttribute("documents", documents);
                request.setAttribute("departments", departments);
                request.setAttribute("selectedClubID", clubID);
            }
            request.setAttribute("userclubs", userclubs);
            request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
            return;
        }

        if ("loadDepartments".equals(action)) {
            int clubID = Integer.parseInt(request.getParameter("clubID"));
            String documentID = request.getParameter("documentID");
            String showDocumentForm = request.getParameter("showDocumentForm");
            boolean hasPermission = false;
            for (UserClub uc : userclubs) {
                if (uc.getClubID() == clubID && (uc.getRoleID() == 1 || uc.getRoleID() == 3)) {
                    hasPermission = true;
                    break;
                }
            }
            if (!hasPermission && documentID != null && !documentID.isEmpty()) {
                request.setAttribute("error", "Bạn không có quyền chỉnh sửa tài liệu này.");
            } else {
                List<Documents> documents = documentsDAO.findByClubID(clubID);
                List<Department> departments = departmentDAO.findByClubID(clubID);
                request.setAttribute("documents", documents);
                request.setAttribute("departments", departments);
                request.setAttribute("selectedClubID", clubID);
                if (documentID != null && !documentID.isEmpty()) {
                    Documents document = documentsDAO.findByDocumentID(Integer.parseInt(documentID));
                    if (document != null) {
                        request.setAttribute("selectedDocumentID", document.getDocumentID());
                        request.setAttribute("selectedDocumentName", document.getDocumentName());
                        request.setAttribute("selectedDescription", document.getDescription());
                        request.setAttribute("selectedDocumentURL", document.getDocumentURL());
                        request.setAttribute("selectedDocumentType", document.getDocumentType());
                        request.setAttribute("selectedDepartmentID", document.getDepartment().getDepartmentID());
                    }
                }
                if ("true".equals(showDocumentForm)) {
                    request.setAttribute("showDocumentForm", true);
                }
            }
            request.setAttribute("userclubs", userclubs);
            request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
            return;
        }

        if ("createDocument".equals(action) || "updateDocument".equals(action)) {
            int clubID = Integer.parseInt(request.getParameter("clubID"));
            String showDocumentForm = request.getParameter("showDocumentForm");
            boolean hasPermission = false;
            for (UserClub uc : userclubs) {
                if (uc.getClubID() == clubID && (uc.getRoleID() == 1 || uc.getRoleID() == 3)) {
                    hasPermission = true;
                    break;
                }
            }
            if (!hasPermission) {
                request.setAttribute("error", "Bạn không có quyền thực hiện hành động này.");
                List<Documents> documents = documentsDAO.findByClubID(clubID);
                List<Department> departments = departmentDAO.findByClubID(clubID);
                request.setAttribute("documents", documents);
                request.setAttribute("departments", departments);
                request.setAttribute("selectedClubID", clubID);
                request.setAttribute("userclubs", userclubs);
                request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
                return;
            }
            Documents document = new Documents();
            document.setDocumentName(request.getParameter("documentName"));
            document.setDescription(request.getParameter("description"));
            document.setDocumentURL(request.getParameter("documentURL"));
            document.setDocumentType(request.getParameter("documentType"));
            Clubs club = new Clubs();
            club.setClubID(clubID);
            document.setClub(club);
            Department department = new Department();
            department.setDepartmentID(Integer.parseInt(request.getParameter("departmentID")));
            document.setDepartment(department);

            try {
                if ("createDocument".equals(action)) {
                    documentsDAO.insertDocument(document);
                    request.setAttribute("message", "Thêm tài liệu thành công!");
                } else {
                    int documentID = Integer.parseInt(request.getParameter("documentID"));
                    document.setDocumentID(documentID);
                    documentsDAO.updateDocument(document);
                    request.setAttribute("message", "Cập nhật tài liệu thành công!");
                }
            } catch (SQLException e) {
                request.setAttribute("error", "Lỗi khi lưu tài liệu: " + e.getMessage());
            }
            List<Documents> documents = documentsDAO.findByClubID(clubID);
            List<Department> departments = departmentDAO.findByClubID(clubID);
            request.setAttribute("documents", documents);
            request.setAttribute("departments", departments);
            request.setAttribute("selectedClubID", clubID);
            if ("true".equals(showDocumentForm)) {
                request.setAttribute("showDocumentForm", true);
                if ("updateDocument".equals(action)) {
                    request.setAttribute("selectedDocumentID", document.getDocumentID());
                    request.setAttribute("selectedDocumentName", document.getDocumentName());
                    request.setAttribute("selectedDescription", document.getDescription());
                    request.setAttribute("selectedDocumentURL", document.getDocumentURL());
                    request.setAttribute("selectedDocumentType", document.getDocumentType());
                    request.setAttribute("selectedDepartmentID", document.getDepartment().getDepartmentID());
                }
            }
            request.setAttribute("userclubs", userclubs);
            request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
            return;
        }

        // Handle other existing POST actions (e.g., create/update meeting)
        request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MyClubController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MyClubController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void createClubMeeting(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        int clubID = Integer.parseInt(request.getParameter("clubId"));
        String startedTime = request.getParameter("startedTime");
        String URLMeeting = request.getParameter("URLMeeting");

        ClubMeetingDAO.insert(clubID, startedTime, URLMeeting);
        String formattedTime = startedTime.replace("T", " ").substring(0, 16);
        String content = "Link tham gia: <a href=\"" + URLMeeting + "\">" + URLMeeting + "</a><br/>Thời gian bắt đầu: <strong>" + formattedTime + "</strong>";
        List<UserClub> userInClub = UserClubDAO.findByClubID(clubID);
        for (UserClub userClub : userInClub) {
            NotificationDAO.sentToPerson(user.getUserID(), userClub.getUserID(), "Cuộc họp mới", content);
        }
        doGet(request, response);
    }

    private void submitUpdateMeeting(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        int clubID = Integer.parseInt(request.getParameter("clubId"));
        String startedTime = request.getParameter("startedTime");
        String URLMeeting = request.getParameter("URLMeeting");
        int clubMeetingID = Integer.parseInt(request.getParameter("clubMeetingId"));
        ClubMeetingDAO.update(clubID, startedTime, URLMeeting, clubMeetingID);
        String formattedTime = startedTime.replace("T", " ").substring(0, 16);
        String content = "Link tham gia: <a href=\"" + URLMeeting + "\">" + URLMeeting + "</a><br/>Thời gian bắt đầu: <strong>" + formattedTime + "</strong>";
        List<UserClub> userInClub = UserClubDAO.findByClubID(clubID);
        for (UserClub userClub : userInClub) {
            NotificationDAO.sentToPerson(user.getUserID(), userClub.getUserID(), "Thay đổi cuộc họp", content);
        }
        doGet(request, response);
    }

    private void deleteClubMeeting(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        int clubMeetingID = Integer.parseInt(request.getParameter("clubMeetingId"));
        ClubMeetingDAO.delete(clubMeetingID);

        doGet(request, response);
    }

}

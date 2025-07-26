/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import dal.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.*;

/**
 *
 * @author LE VAN THUAN
 */
public class ChairmanTasksServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ChairmanTasksServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChairmanTasksServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user != null) {
            String userID = user.getUserID();
            ClubDAO clubDAO = new ClubDAO();
            EventsDAO eventDAO = new EventsDAO();
            TaskDAO taskDAO = new TaskDAO();
            DepartmentDAO deptDAO = new DepartmentDAO();
            DocumentsDAO docDAO = new DocumentsDAO();

            ClubInfo club = clubDAO.getClubChairman(userID);
            List<Events> eventList = eventDAO.getEventsByClubIdForTask(club.getClubID());
            List<Department> departmentList = deptDAO.getDepartmentsByClubID(club.getClubID());
            List<Documents> documentsList = docDAO.getDocumentsByClubID(club.getClubID());

            String eventIDParam = request.getParameter("eventID");
            Map<Events, Map<String, List<Tasks>>> timelineMap = new LinkedHashMap<>();

            if (eventIDParam != null && !eventIDParam.isEmpty()) {
                try {
                    int eventID = Integer.parseInt(eventIDParam);
                    Events selectedEvent = eventDAO.getEventByID(eventID);

                    List<EventTerms> allTerms = taskDAO.getTermsByEventID(eventID);

                    if (!allTerms.isEmpty()) {
                        List<Tasks> allTasks = taskDAO.getTasksByEventID(eventID);

                        for (Tasks task : allTasks) {
                            Department dept = deptDAO.getDepartmentByID(task.getDepartmentAssignee().getDepartmentID());
                            task.setDepartmentAssignee(dept);
                            if (task.getDocument() != null) {
                                Documents doc = docDAO.getDocumentByID(task.getDocument().getDocumentID());
                                task.setDocument(doc);
                            }
                        }

                        Map<String, List<Tasks>> groupedByTerm = new LinkedHashMap<>();
                        Map<String, EventTerms> termInfoMap = new LinkedHashMap<>();

                        for (EventTerms term : allTerms) {
                            groupedByTerm.put(term.getTermName(), new ArrayList<>());
                            termInfoMap.put(term.getTermName(), term);
                        }

                        for (Tasks task : allTasks) {
                            String termName = task.getTerm().getTermName();
                            if (groupedByTerm.containsKey(termName)) {
                                groupedByTerm.get(termName).add(task);
                            }
                        }

                        timelineMap.put(selectedEvent, groupedByTerm);
                        request.setAttribute("termInfoMap_" + eventID, termInfoMap);
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            } else {
                for (Events event : eventList) {
                    List<EventTerms> allTerms = taskDAO.getTermsByEventID(event.getEventID());

                    if (!allTerms.isEmpty()) {
                        List<Tasks> allTasks = taskDAO.getTasksByEventID(event.getEventID());

                        for (Tasks task : allTasks) {
                            Department dept = deptDAO.getDepartmentByID(task.getDepartmentAssignee().getDepartmentID());
                            task.setDepartmentAssignee(dept);
                            if (task.getDocument() != null) {
                                Documents doc = docDAO.getDocumentByID(task.getDocument().getDocumentID());
                                task.setDocument(doc);
                            }
                        }

                        Map<String, List<Tasks>> groupedByTerm = new LinkedHashMap<>();
                        Map<String, EventTerms> termInfoMap = new LinkedHashMap<>();

                        for (EventTerms term : allTerms) {
                            groupedByTerm.put(term.getTermName(), new ArrayList<>());
                            termInfoMap.put(term.getTermName(), term);
                        }

                        for (Tasks task : allTasks) {
                            String termName = task.getTerm().getTermName();
                            if (groupedByTerm.containsKey(termName)) {
                                groupedByTerm.get(termName).add(task);
                            }
                        }

                        timelineMap.put(event, groupedByTerm);
                        request.setAttribute("termInfoMap_" + event.getEventID(), termInfoMap);
                    }
                }
            }

            request.setAttribute("timelineMap", timelineMap);
            request.setAttribute("eventList", eventList);
            request.setAttribute("club", club);
            request.setAttribute("departmentList", departmentList);
            request.setAttribute("documentsList", documentsList);
            request.setAttribute("currentPath", request.getServletPath());

            request.getRequestDispatcher("/view/student/chairman/tasks.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("addTask".equals(action)) {
            try {
                // Lấy dữ liệu từ form
                int termID = Integer.parseInt(request.getParameter("termID"));
                int eventID = Integer.parseInt(request.getParameter("eventID"));
                int clubID = Integer.parseInt(request.getParameter("clubID"));
                int departmentID = Integer.parseInt(request.getParameter("departmentID"));
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String existingDocumentID = request.getParameter("existingDocumentID");
                String documentName = request.getParameter("documentName");
                String documentURL = request.getParameter("documentURL");
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                String createdBy = request.getParameter("createdBy");
                String assigneeType = request.getParameter("assigneeType");

                if ("new".equals(existingDocumentID) && documentURL != null && !documentURL.isEmpty() && (documentName == null || documentName.isEmpty())) {
                    request.setAttribute("errorMessage", "Vui lòng nhập tên tài liệu khi cung cấp liên kết tài liệu.");
                    request.setAttribute("showTermModal", true);
                    doGet(request, response);
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = new Date(sdf.parse(startDateStr).getTime());
                Date endDate = new Date(sdf.parse(endDateStr).getTime());

                // Kiểm tra ngày hợp lệ với EventTerms
                TaskDAO taskDAO = new TaskDAO();
                EventTerms term = taskDAO.getEventTermsByID(termID);
                if (term == null || startDate.before(term.getTermStart()) || endDate.after(term.getTermEnd())) {
                    request.setAttribute("errorMessage", "Ngày bắt đầu và kết thúc phải nằm trong khoảng thời gian của giai đoạn.");
                    request.setAttribute("showTermModal", true);
                    doGet(request, response);
                    return;
                }

                // Xử lý tài liệu
                Integer documentID = null;
                if ("new".equals(existingDocumentID) && documentURL != null && !documentURL.isEmpty()) {
                    DocumentsDAO docDAO = new DocumentsDAO();
                    Documents doc = new Documents();
                    doc.setDocumentName(documentName);
                    doc.setDocumentURL(documentURL);
                    doc.setDocumentType("Tasks");
                    Clubs c = new Clubs();
                    c.setClubID(clubID);
                    doc.setClub(c);
                    Department d = new Department();
                    d.setDepartmentID(departmentID);
                    doc.setDepartment(d);
                    documentID = docDAO.addDocument(doc);
                    if (documentID == null) {
                        request.setAttribute("errorMessage", "Không thể lưu tài liệu.");
                        request.setAttribute("showTermModal", true);
                        doGet(request, response);
                        return;
                    }
                } else if (!existingDocumentID.isEmpty() && !"new".equals(existingDocumentID)) {
                    documentID = Integer.parseInt(existingDocumentID);
                }

                Tasks task = new Tasks();
                EventTerms et = new EventTerms();
                et.setTermID(termID);
                Events e = new Events();
                e.setEventID(eventID);
                Clubs c = new Clubs();
                c.setClubID(clubID);
                Department d = new Department();
                d.setDepartmentID(departmentID);
                Users u = new Users();
                u.setUserID(createdBy);
                task.setTerm(et);
                task.setEvent(e);
                task.setClub(c);
                task.setAssigneeType(assigneeType);
                task.setDepartmentAssignee(d);
                task.setTitle(title);
                task.setDescription(description);
                task.setStatus("ToDo");
                task.setStartDate(startDate);
                task.setEndDate(endDate);
                task.setCreatedBy(u);
                if (documentID != null) {
                    Documents doc = new Documents();
                    doc.setDocumentID(documentID);
                    task.setDocument(doc);
                }

                boolean success = taskDAO.addTask(task);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/chairman-page/tasks?eventID=" + eventID);
                } else {
                    request.setAttribute("errorMessage", "Không thể thêm công việc.");
                    request.setAttribute("showTermModal", true);

                    doGet(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
                doGet(request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }



    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

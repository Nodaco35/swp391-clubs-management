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
public class ChairmanEditTasksServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            out.println("<title>Servlet ChairmanEditTasksServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChairmanEditTasksServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
            String taskIDParam = request.getParameter("taskID");
            if (taskIDParam == null || taskIDParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/chairman-page/tasks");
                return;
            }

            try {
                int taskID = Integer.parseInt(taskIDParam);
                TaskDAO taskDAO = new TaskDAO();
                Tasks task = taskDAO.getTasksByID(taskID);
                if (task == null) {
                    request.setAttribute("errorMessage", "Công việc không tồn tại.");
                    response.sendRedirect(request.getContextPath() + "/chairman-page/tasks");
                    return;
                }

                ClubDAO clubDAO = new ClubDAO();
                DepartmentDAO deptDAO = new DepartmentDAO();
                DocumentsDAO docDAO = new DocumentsDAO();

                ClubInfo club = clubDAO.getClubChairman(user.getUserID());
                List<Department> departmentList = deptDAO.getDepartmentsByClubID(club.getClubID());
                List<Documents> documentsList = docDAO.getDocumentsByClubID(club.getClubID());

                request.setAttribute("task", task);
                request.setAttribute("club", club);
                request.setAttribute("departmentList", departmentList);
                request.setAttribute("documentsList", documentsList);
                request.setAttribute("currentPath", request.getServletPath());

                request.getRequestDispatcher("/view/student/chairman/edit-tasks.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("updateTask".equals(action)) {
            try {
                // Lấy dữ liệu từ form
                int taskID = Integer.parseInt(request.getParameter("taskID"));
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
                String status = request.getParameter("status");
                String assigneeType = request.getParameter("assigneeType");

                // Validation
                if ("new".equals(existingDocumentID) && documentURL != null && !documentURL.isEmpty() && (documentName == null || documentName.isEmpty())) {
                    request.setAttribute("errorMessage", "Vui lòng nhập tên tài liệu khi cung cấp liên kết tài liệu.");
                    doGet(request, response);
                    return;
                }

                // Chuyển đổi ngày
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = new Date(sdf.parse(startDateStr).getTime());
                Date endDate = new Date(sdf.parse(endDateStr).getTime());

                // Kiểm tra ngày hợp lệ với EventTerms
                TaskDAO taskDAO = new TaskDAO();
                EventTerms term = taskDAO.getEventTermsByID(termID);
                if (term == null || startDate.before(term.getTermStart()) || endDate.after(term.getTermEnd())) {
                    request.setAttribute("errorMessage", "Ngày bắt đầu và kết thúc phải nằm trong khoảng thời gian của giai đoạn.");
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
                        doGet(request, response);
                        return;
                    }
                } else if (!existingDocumentID.isEmpty() && !"new".equals(existingDocumentID)) {
                    documentID = Integer.parseInt(existingDocumentID);
                }

                // Tạo đối tượng Tasks
                Tasks task = new Tasks();
                task.setTaskID(taskID);
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
                task.setStatus(status);
                task.setStartDate(startDate);
                task.setEndDate(endDate);
                task.setCreatedBy(u);
                if (documentID != null) {
                    Documents doc = new Documents();
                    doc.setDocumentID(documentID);
                    task.setDocument(doc);
                }

                // Cập nhật vào database
                boolean success = taskDAO.updateTask(task);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/edit-tasks?taskID=" + taskID);
                } else {
                    request.setAttribute("errorMessage", "Không thể cập nhật công việc.");
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

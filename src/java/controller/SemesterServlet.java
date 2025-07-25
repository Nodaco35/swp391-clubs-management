package controller;

import dal.TermDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Term;
import models.Users;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.sql.SQLException;

public class SemesterServlet extends HttpServlet {

    private TermDAO termDAO;

    @Override
    public void init() throws ServletException {
        termDAO = new TermDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String search = request.getParameter("search");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 10; // Number of semesters per page

        if (action == null || action.equals("manageSemesters")) {
            List<Term> semesters = termDAO.getPaginatedSemesters(search, page, pageSize);
            int totalSemesters = termDAO.getTotalSemesters(search);
            int totalPages = (int) Math.ceil((double) totalSemesters / pageSize);

            request.setAttribute("semesters", semesters);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/view/ic/semesters.jsp").forward(request, response);
        } else if (action.equals("deleteSemester")) {
            String termID = request.getParameter("id");
            try {
                boolean success = termDAO.deleteSemester(termID);
                request.setAttribute("message", success ? "Xóa kỳ học thành công!" : "Xóa kỳ học thất bại!");
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("error", "Không thể xóa kỳ học do có dữ liệu liên quan (báo cáo, sự kiện, v.v.)!");
            }
            List<Term> semesters = termDAO.getPaginatedSemesters(search, page, pageSize);
            int totalSemesters = termDAO.getTotalSemesters(search);
            int totalPages = (int) Math.ceil((double) totalSemesters / pageSize);

            request.setAttribute("semesters", semesters);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/view/ic/semesters.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String search = request.getParameter("search");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 10;

        if (action.equals("addSemester")) {
            Term term = new Term();
            String termID = request.getParameter("termID") != null ? request.getParameter("termID").trim() : "";
            String termName = request.getParameter("termName") != null ? request.getParameter("termName").trim() : "";
            term.setTermID(termID);
            term.setTermName(termName);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                term.setStartDate(sdf.parse(request.getParameter("startDate")));
                term.setEndDate(sdf.parse(request.getParameter("endDate")));
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Định dạng ngày không hợp lệ!");
                List<Term> semesters = termDAO.getPaginatedSemesters(search, page, pageSize);
                int totalSemesters = termDAO.getTotalSemesters(search);
                int totalPages = (int) Math.ceil((double) totalSemesters / pageSize);
                request.setAttribute("semesters", semesters);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.getRequestDispatcher("/view/ic/semesters.jsp").forward(request, response);
                return;
            }
            term.setStatus(request.getParameter("status"));
            // Validate TermID and TermName
            if (!termID.matches("(FA|SP|SU)[0-9]{2}")) {
                request.setAttribute("error", "Mã kỳ không đúng định dạng (VD: SP25, SU25, FA24)! Nhập: " + termID);
            } else if (!termName.matches("(Spring|Summer|Fall) [0-9]{4}")) {
                request.setAttribute("error", "Tên kỳ không đúng định dạng (VD: Spring 2025, Summer 2025, Fall 2024)! Nhập: " + termName);
            } else if (!isValidSemesterDates(term.getStartDate(), term.getEndDate())) {
                request.setAttribute("error", "Ngày kết thúc phải thuộc tháng cách ngày bắt đầu 4 tháng (VD: 2025-09-01 -> 2025-12-31)!");
            } else if (termDAO.termExists(termID)) {
                request.setAttribute("error", "Mã kỳ " + termID + " đã tồn tại!");
            } else {
                try {
                    boolean success = termDAO.addSemester(term);
                    request.setAttribute("message", success ? "Thêm kỳ học thành công!" : "Thêm kỳ học thất bại!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Lỗi khi thêm kỳ học: " + e.getMessage());
                }
            }
            List<Term> semesters = termDAO.getPaginatedSemesters(search, page, pageSize);
            int totalSemesters = termDAO.getTotalSemesters(search);
            int totalPages = (int) Math.ceil((double) totalSemesters / pageSize);
            request.setAttribute("semesters", semesters);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/view/ic/semesters.jsp").forward(request, response);
        } else if (action.equals("editSemester")) {
            Term term = new Term();
            String termID = request.getParameter("termID") != null ? request.getParameter("termID").trim() : "";
            String termName = request.getParameter("termName") != null ? request.getParameter("termName").trim() : "";
            term.setTermID(termID);
            term.setTermName(termName);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                term.setStartDate(sdf.parse(request.getParameter("startDate")));
                term.setEndDate(sdf.parse(request.getParameter("endDate")));
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Định dạng ngày không hợp lệ!");
                List<Term> semesters = termDAO.getPaginatedSemesters(search, page, pageSize);
                int totalSemesters = termDAO.getTotalSemesters(search);
                int totalPages = (int) Math.ceil((double) totalSemesters / pageSize);
                request.setAttribute("semesters", semesters);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.getRequestDispatcher("/view/ic/semesters.jsp").forward(request, response);
                return;
            }
            term.setStatus(request.getParameter("status"));
            // Validate TermName and dates
            if (!termName.matches("(Spring|Summer|Fall) [0-9]{4}")) {
                request.setAttribute("error", "Tên kỳ không đúng định dạng (VD: Spring 2025, Summer 2025, Fall 2024)! Nhập: " + termName);
            } else if (!isValidSemesterDates(term.getStartDate(), term.getEndDate())) {
                request.setAttribute("error", "Ngày kết thúc phải thuộc tháng cách ngày bắt đầu 4 tháng (VD: 2025-09-01 -> 2025-12-31)!");
            } else {
                try {
                    boolean success = termDAO.updateSemester(termID, term);
                    request.setAttribute("message", success ? "Cập nhật kỳ học thành công!" : "Cập nhật kỳ học thất bại!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Lỗi khi cập nhật kỳ học: " + e.getMessage());
                }
            }
            List<Term> semesters = termDAO.getPaginatedSemesters(search, page, pageSize);
            int totalSemesters = termDAO.getTotalSemesters(search);
            int totalPages = (int) Math.ceil((double) totalSemesters / pageSize);
            request.setAttribute("semesters", semesters);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/view/ic/semesters.jsp").forward(request, response);
        }
    }

    private boolean isValidSemesterDates(java.util.Date startDate, java.util.Date endDate) {
        if (endDate.before(startDate)) {
            return false;
        }
        // Convert to LocalDate for month-based validation
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // Check if endDate is in the month that is 4 months after startDate
        LocalDate expectedEndMonth = startLocalDate.plusMonths(3);
        return endLocalDate.getYear() == expectedEndMonth.getYear() &&
               endLocalDate.getMonth() == expectedEndMonth.getMonth();
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing semesters in the IC Officer Portal";
    }
}
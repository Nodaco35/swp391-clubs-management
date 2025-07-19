package controller;

import dal.DepartmentDashboardDAO;
import dal.FinancialDAO;
import dal.TermDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import models.*;

public class DF_ExpenseServlet extends HttpServlet {

    private DepartmentDashboardDAO dashboardDAO;

    @Override
    public void init() throws ServletException {
        dashboardDAO = new DepartmentDashboardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Retrieve clubID from request or session
        String clubIDParam = request.getParameter("clubID");
        Integer sessionClubID = (Integer) request.getSession().getAttribute("clubID");
        int clubID;
        if (clubIDParam != null && !clubIDParam.isEmpty()) {
            try {
                clubID = Integer.parseInt(clubIDParam);
                request.getSession().setAttribute("clubID", clubID); // Update session
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid clubID format");
                return;
            }
        } else if (sessionClubID != null) {
            clubID = sessionClubID;
        } else {
            response.sendRedirect(request.getContextPath() + "/myclub?error=clubID_required");
            return;
        }

        Term term = TermDAO.getActiveSemester();
        if (term.getTermID() == null) {
            request.setAttribute("error", "Hiện chưa có kỳ nào hoạt động nên không thể quản lý chi tiêu");
            request.getRequestDispatcher("/myclub").forward(request, response);
            return;
        }

        if (!dashboardDAO.isDepartmentLeaderIndoingoai(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }

        // Set attributes for sidebar
        request.setAttribute("isAccess", dashboardDAO.isDepartmentLeaderIndoingoai(user.getUserID(), clubID));
        request.setAttribute("activeMenu", "financial");
        request.setAttribute("activeSubMenu", "expense");
        request.setAttribute("clubID", clubID);

        // Get filter parameters
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status") != null ? request.getParameter("status") : "all";

        // Pagination
        int page = 1;
        int pageSize = 7;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Retrieve paginated expenses and total records
        List<Expenses> expensesList = FinancialDAO.getExpenses(clubID, term.getTermID(), keyword, status, page, pageSize);
        int totalRecords = FinancialDAO.getTotalExpenseRecords(clubID, term.getTermID(), keyword, status);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // Set attributes for JSP
        request.setAttribute("expensesList", expensesList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("term", term);
        request.getRequestDispatcher("/view/student/department-leader/financial-expense.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Retrieve clubID from request or session
        String clubIDParam = request.getParameter("clubID");
        Integer sessionClubID = (Integer) request.getSession().getAttribute("clubID");
        int clubID;
        if (clubIDParam != null && !clubIDParam.isEmpty()) {
            try {
                clubID = Integer.parseInt(clubIDParam);
                request.getSession().setAttribute("clubID", clubID); // Update session
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid clubID format");
                return;
            }
        } else if (sessionClubID != null) {
            clubID = sessionClubID;
        } else {
            response.sendRedirect(request.getContextPath() + "/myclub?error=clubID_required");
            return;
        }

        Term term = (Term) request.getSession().getAttribute("term");
        if (term == null) {
            term = TermDAO.getActiveSemester();
            request.getSession().setAttribute("term", term);
        }

        switch (action) {
            case "addExpense":
                try {
                    Expenses expense = new Expenses();
                    expense.setClubID(clubID);
                    expense.setTermID(term.getTermID());
                    expense.setPurpose(request.getParameter("purpose"));
                    expense.setAmount(new BigDecimal(request.getParameter("amount")));
                    // Parse date input (yyyy-MM-dd)
                    String dateStr = request.getParameter("expenseDate");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date utilDate = sdf.parse(dateStr);
                        expense.setExpenseDate(new Date(utilDate.getTime()));
                    } else {
                        expense.setExpenseDate(new Date(System.currentTimeMillis())); // Default to today if empty
                    }
                    expense.setDescription(request.getParameter("description"));
                    String attachment = request.getParameter("attachment");
                    if (attachment != null && !attachment.isEmpty()) {
                        if (!attachment.contains("docs.google.com/spreadsheets") && !attachment.contains("office.com/excel")) {
                            request.setAttribute("error", "Link đính kèm phải là link Excel online (Google Sheets hoặc Office Online)!");
                            doGet(request, response);
                            return;
                        }
                    }
                    expense.setAttachment(attachment);
                    if (FinancialDAO.addExpense(expense, user.getUserID())) {
                        request.setAttribute("message", "Thêm chi tiêu thành công!");
                    } else {
                        request.setAttribute("error", "Không thể thêm chi tiêu!");
                    }
                } catch (ParseException e) {
                    request.setAttribute("error", "Định dạng ngày không hợp lệ!");
                } catch (Exception e) {
                    request.setAttribute("error", "Lỗi khi thêm chi tiêu: " + e.getMessage());
                }
                break;

            case "approveExpense":
                try {
                    int expenseID = Integer.parseInt(request.getParameter("expenseID"));
                    Expenses expense = FinancialDAO.getExpenseByID(expenseID);
                    if (expense == null || expense.isApproved()) {
                        request.setAttribute("error", "Chi tiêu không tồn tại hoặc đã được phê duyệt!");
                    } else {
                        BigDecimal income = FinancialDAO.getTotalIncomeAmount(clubID, term.getTermID());
                        BigDecimal expenses = FinancialDAO.getTotalExpenseAmount(clubID, term.getTermID());
                        BigDecimal balance = income.subtract(expenses);
                        if (balance.compareTo(expense.getAmount()) < 0) {
                            request.setAttribute("error", "Quỹ của CLB không đủ để phê duyệt chi tiêu này!");
                        } else {
                            if (FinancialDAO.approveExpense(expenseID, user.getUserID())) {
                                request.setAttribute("message", "Phê duyệt chi tiêu thành công!");
                            } else {
                                request.setAttribute("error", "Không thể phê duyệt chi tiêu!");
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "ID chi tiêu không hợp lệ!");
                }
                break;
            default:
                request.setAttribute("error", "Hành động không hợp lệ!");
                break;
        }
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing club expenses";
    }
}
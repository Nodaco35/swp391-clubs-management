package controller;

import dal.DepartmentDashboardDAO;
import dal.ExpenseDAO;
import dal.NotificationDAO;
import dal.TermDAO;
import models.Expenses;
import models.Users;
import models.Term;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ApproveExpenseServlet extends HttpServlet {

    private DepartmentDashboardDAO dashboardDAO;
    private ExpenseDAO expenseDAO;
    private TermDAO termDAO;

    @Override
    public void init() throws ServletException {
        dashboardDAO = new DepartmentDashboardDAO();
        expenseDAO = new ExpenseDAO();
        termDAO = new TermDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Integer clubID = (Integer) request.getSession().getAttribute("clubID");
        if (clubID == null || !dashboardDAO.isDepartmentLeaderIndoingoai(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền duyệt báo cáo chi phí");
            return;
        }

        // Fetch active term
        Term term = termDAO.getActiveSemester();
        if (term == null || term.getTermID() == null) {
            request.setAttribute("error", "Không tìm thấy kỳ học đang hoạt động. Vui lòng liên hệ quản trị viên.");
            request.getRequestDispatcher("/view/student/department-leader/approve-expense.jsp").forward(request, response);
            return;
        }

        // Fetch pending expenses for the active term
        List<Expenses> pendingExpenses = expenseDAO.getPendingExpenses(clubID, term.getTermID());
        request.setAttribute("term", term);
        request.setAttribute("pendingExpenses", pendingExpenses);
        request.getRequestDispatcher("/view/student/department-leader/approve-expense.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Integer clubID = (Integer) request.getSession().getAttribute("clubID");
        if (clubID == null || !dashboardDAO.isDepartmentLeaderIndoingoai(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền duyệt báo cáo chi phí");
            return;
        }

        // Fetch active term
        Term term = termDAO.getActiveSemester();
        if (term == null || term.getTermID() == null) {
            request.setAttribute("error", "Không tìm thấy kỳ học đang hoạt động. Vui lòng liên hệ quản trị viên.");
            doGet(request, response);
            return;
        }

        try {
            String action = request.getParameter("action");
            int expenseID = Integer.parseInt(request.getParameter("expenseID"));
            String status = action.equals("approve") ? "Approved" : "Rejected";

            // Verify the expense belongs to the active term
            Expenses expense = expenseDAO.getPendingExpenses(clubID, term.getTermID()).stream()
                    .filter(e -> e.getExpenseID() == expenseID)
                    .findFirst()
                    .orElse(null);
            if (expense == null) {
                request.setAttribute("error", "Báo cáo chi phí không hợp lệ hoặc không thuộc kỳ học hiện tại.");
                doGet(request, response);
                return;
            }

            // Update expense status
            boolean success = expenseDAO.updateExpenseStatus(expenseID, status, user.getUserID());
            if (success) {
                // If approved, add to Transactions table
                if (status.equals("Approved")) {
                    expenseDAO.addTransactionForApprovedExpense(expense, user.getUserID());
                }
                // Send notification to the creator
                NotificationDAO.sentToPerson1(
                    user.getUserID(),
                    expense.getCreatedBy(),
                    "Trạng Thái Báo Cáo Chi Phí",
                    "Báo cáo chi phí của bạn (" + expense.getDescription() + ") đã được " + (status.equals("Approved") ? "duyệt" : "từ chối") + " bởi " + user.getFullName(),
                    "HIGH"
                );
                request.setAttribute("message", "Báo cáo chi phí đã được " + (status.equals("Approved") ? "duyệt" : "từ chối") + " thành công!");
            } else {
                request.setAttribute("error", "Không thể cập nhật trạng thái báo cáo chi phí. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        doGet(request, response);
    }
}
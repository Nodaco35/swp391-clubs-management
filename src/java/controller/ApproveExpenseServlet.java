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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }

        // Fetch active term
        Term term = termDAO.getActiveSemester();
        if (term == null || term.getTermID() == null) {
            request.setAttribute("error", "Không tìm thấy kỳ học đang hoạt động. Vui lòng liên hệ quản trị viên.");
            request.getRequestDispatcher("/view/student/department-leader/approve-expense.jsp").forward(request, response);
            return;
        }

        // Get pagination, search, and filter parameters
        int page = 1;
        int pageSize = 5; // Match submit-expense.jsp
        String search = request.getParameter("search");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        // Initialize message and error attributes
        String message = request.getAttribute("message") != null ? (String) request.getAttribute("message") : "";
        String error = request.getAttribute("error") != null ? (String) request.getAttribute("error") : "";

        // Fetch expenses
        List<Expenses> expenses = expenseDAO.getAllExpenses(clubID, term.getTermID(), (page - 1) * pageSize, pageSize, search, status, sortBy);
        int totalExpenses = expenseDAO.getAllExpensesCount(clubID, term.getTermID(), search, status);
        int totalPages = (int) Math.ceil((double) totalExpenses / pageSize);

        request.setAttribute("term", term);
        request.setAttribute("expenses", expenses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("message", message);
        request.setAttribute("error", error);
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
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }

        Term term = termDAO.getActiveSemester();
        if (term == null || term.getTermID() == null) {
            request.setAttribute("error", "Không tìm thấy kỳ học đang hoạt động. Vui lòng liên hệ quản trị viên.");
            doGet(request, response);
            return;
        }

        try {
            String action = request.getParameter("action");
            if ("submit".equals(action)) {
                String purpose = request.getParameter("purpose");
                String amountStr = request.getParameter("amount");
                String expenseDateStr = request.getParameter("expenseDate");
                String description = request.getParameter("description");
                String attachment = request.getParameter("attachment");

                if (purpose == null || purpose.trim().isEmpty()) {
                    request.setAttribute("error", "Mục đích không được để trống.");
                    doGet(request, response);
                    return;
                }
                if (amountStr == null || amountStr.trim().isEmpty()) {
                    request.setAttribute("error", "Số tiền không được để trống.");
                    doGet(request, response);
                    return;
                }
                if (expenseDateStr == null || expenseDateStr.trim().isEmpty()) {
                    request.setAttribute("error", "Ngày chi tiêu không được để trống.");
                    doGet(request, response);
                    return;
                }

                String[] validPurposes = {"Sự kiện", "Vật tư", "Thuê địa điểm", "Khác"};
                boolean isValidPurpose = false;
                for (String validPurpose : validPurposes) {
                    if (validPurpose.equals(purpose)) {
                        isValidPurpose = true;
                        break;
                    }
                }
                if (!isValidPurpose) {
                    request.setAttribute("error", "Mục đích không hợp lệ.");
                    doGet(request, response);
                    return;
                }

                if (attachment != null && !attachment.trim().isEmpty()) {
                    if (!attachment.matches("^https://docs\\.google\\.com/.*")) {
                        request.setAttribute("error", "Link Google Docs không hợp lệ. Vui lòng nhập link bắt đầu bằng https://docs.google.com/");
                        doGet(request, response);
                        return;
                    }
                }

                BigDecimal amount;
                try {
                    amount = new BigDecimal(amountStr);
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        request.setAttribute("error", "Số tiền phải lớn hơn 0.");
                        doGet(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Số tiền không hợp lệ.");
                    doGet(request, response);
                    return;
                }

                Timestamp expenseDate;
                try {
                    expenseDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(expenseDateStr).getTime());
                } catch (Exception e) {
                    request.setAttribute("error", "Ngày chi tiêu không hợp lệ.");
                    doGet(request, response);
                    return;
                }

                Expenses expense = new Expenses();
                expense.setClubID(clubID);
                expense.setTermID(term.getTermID());
                expense.setPurpose(purpose);
                expense.setAmount(amount);
                expense.setExpenseDate(expenseDate);
                expense.setDescription(description != null ? description : "");
                expense.setAttachment(attachment != null && !attachment.trim().isEmpty() ? attachment : null);

                boolean success = expenseDAO.submitExpense(expense, user.getUserID());
                if (success) {
                    request.setAttribute("message", "Đơn xin chi phí đã được gửi thành công!");
                } else {
                    request.setAttribute("error", "Không thể gửi đơn xin chi phí. Vui lòng thử lại.");
                }
            } else {
                int expenseID;
                try {
                    expenseID = Integer.parseInt(request.getParameter("expenseID"));
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "ID đơn xin chi phí không hợp lệ.");
                    doGet(request, response);
                    return;
                }

                Expenses expense = expenseDAO.getExpenseById(expenseID);
                if (expense == null || expense.getClubID() != clubID) {
                    request.setAttribute("error", "Đơn xin chi phí không hợp lệ hoặc không thuộc CLB này.");
                    doGet(request, response);
                    return;
                }

                String status = action.equals("approve") ? "Approved" : "Rejected";
                if (status.equals("Approved")) {
                    BigDecimal balance = expenseDAO.getClubBalance(clubID, term.getTermID());
                    if (balance.compareTo(expense.getAmount()) < 0) {
                        request.setAttribute("error", "Quỹ không đủ để duyệt đơn xin chi phí này.");
                        doGet(request, response);
                        return;
                    }
                }
                String RejectContent = request.getParameter("reason");
                boolean success = expenseDAO.updateExpenseStatus(expenseID, status, user.getUserID(), RejectContent);
                if (success) {
                    if (status.equals("Approved")) {
                        expenseDAO.addTransactionForApprovedExpense(expense, user.getUserID());
                    }
                    NotificationDAO.sentToPerson1(
                            user.getUserID(),
                            expense.getCreatedBy(),
                            "Trạng Thái Đơn xin Chi Phí",
                            "Đơn xin chi phí của bạn (" + expense.getDescription() + ") đã được "
                            + (status.equals("Approved") ? "duyệt" : "từ chối")
                            + (status.equals("Rejected") ? " với lý do: " + "'"+ RejectContent + "'": "")
                            + " bởi " + user.getFullName(),
                            "HIGH"
                    );
                    request.setAttribute("message", "Đơn xin chi phí đã được " + (status.equals("Approved") ? "duyệt" : "từ chối") + " thành công!");
                } else {
                    request.setAttribute("error", "Không thể cập nhật trạng thái đơn xin chi phí. Vui lòng thử lại.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        doGet(request, response);
    }
}

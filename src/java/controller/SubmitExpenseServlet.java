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
import java.util.logging.Logger;

public class SubmitExpenseServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SubmitExpenseServlet.class.getName());
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
        if (clubID == null || !dashboardDAO.isDepartmentLeaderHauCan(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền gửi báo cáo chi phí");
            return;
        }

        // Fetch active term
        Term term = termDAO.getActiveSemester();
        if (term == null || term.getTermID() == null) {
            request.setAttribute("error", "Không tìm thấy kỳ học đang hoạt động. Vui lòng liên hệ quản trị viên.");
            request.getRequestDispatcher("/view/student/department-leader/submit-expense.jsp").forward(request, response);
            return;
        }

        // Fetch submitted expenses for the user
        List<Expenses> submittedExpenses = expenseDAO.getUserSubmittedExpenses(user.getUserID(), clubID, term.getTermID());
        request.setAttribute("term", term);
        request.setAttribute("submittedExpenses", submittedExpenses);

        request.getRequestDispatcher("/view/student/department-leader/submit-expense.jsp").forward(request, response);
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
        if (clubID == null || !dashboardDAO.isDepartmentLeaderHauCan(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền gửi báo cáo chi phí");
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
            // Validate form parameters
            String purpose = request.getParameter("purpose");
            String amountStr = request.getParameter("amount");
            String expenseDateStr = request.getParameter("expenseDate");
            String description = request.getParameter("description");
            String attachment = request.getParameter("attachment");

            // Debug logging
            LOGGER.info("Received purpose: " + (purpose != null ? purpose : "null"));
            LOGGER.info("Received amount: " + (amountStr != null ? amountStr : "null"));
            LOGGER.info("Received expenseDate: " + (expenseDateStr != null ? expenseDateStr : "null"));
            LOGGER.info("Received attachment: " + (attachment != null ? attachment : "null"));

            // Check for null or empty required fields
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

            // Validate purpose against allowed values
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

            // Validate Google Docs link (optional)
            if (attachment != null && !attachment.trim().isEmpty()) {
                if (!attachment.matches("^https://docs\\.google\\.com/.*")) {
                    request.setAttribute("error", "Link Google Docs không hợp lệ. Vui lòng nhập link bắt đầu bằng https://docs.google.com/");
                    doGet(request, response);
                    return;
                }
            }

            // Parse amount
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

            // Parse date
            Timestamp expenseDate;
            try {
                expenseDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(expenseDateStr).getTime());
            } catch (Exception e) {
                request.setAttribute("error", "Ngày chi tiêu không hợp lệ.");
                doGet(request, response);
                return;
            }

            // Create expense object
            Expenses expense = new Expenses();
            expense.setClubID(clubID);
            expense.setTermID(term.getTermID()); // Use active term
            expense.setPurpose(purpose);
            expense.setAmount(amount);
            expense.setExpenseDate(expenseDate);
            expense.setDescription(description != null ? description : "");
            expense.setAttachment(attachment != null && !attachment.trim().isEmpty() ? attachment : null);

            // Submit expense
            boolean success = expenseDAO.submitExpense(expense, user.getUserID());
            if (success) {
                // Send notification to External Affairs Leader
                String externalLeaderID = dashboardDAO.getExternalLeaderID(clubID);
                if (externalLeaderID != null) {
                    NotificationDAO.sentToPerson1(
                        user.getUserID(),
                        externalLeaderID,
                        "Báo Cáo Chi Phí Mới",
                        "Một báo cáo chi phí mới đã được gửi từ " + user.getFullName() + " cho CLB " + clubID + ". Vui lòng xem xét và duyệt.",
                        "HIGH"
                    );
                }
                request.setAttribute("message", "Báo cáo chi phí đã được gửi thành công!");
            } else {
                request.setAttribute("error", "Không thể gửi báo cáo chi phí. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        doGet(request, response); // Refresh the page to show updated list
    }
}
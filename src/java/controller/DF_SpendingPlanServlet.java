// Full updated code for DF_SpendingPlanServlet.java
package controller;

import dal.DepartmentDashboardDAO;
import dal.FinancialDAO;
import dal.TermDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import models.*;

public class DF_SpendingPlanServlet extends HttpServlet {

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

        String clubIDParam = request.getParameter("clubID");
        Integer sessionClubID = (Integer) request.getSession().getAttribute("clubID");
        int clubID;
        if (clubIDParam != null && !clubIDParam.isEmpty()) {
            try {
                clubID = Integer.parseInt(clubIDParam);
                request.getSession().setAttribute("clubID", clubID);
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
            request.setAttribute("error", "Hiện chưa có kỳ nào hoạt động nên không thể quản lý kế hoạch chi tiêu");
            request.getRequestDispatcher("/myclub").forward(request, response);
            return;
        }

        if (!dashboardDAO.isDepartmentLeaderHauCan(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }
        
        boolean accessSpending = dashboardDAO.isDepartmentLeaderHauCan(user.getUserID(), clubID);
        request.getSession().setAttribute("isAccessSpending", accessSpending);
        request.setAttribute("isAccessSpending", accessSpending);
        
        request.setAttribute("activeMenu", "financial");
        request.setAttribute("activeSubMenu", "spending-plan");
        request.setAttribute("clubID", clubID);

        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status") != null ? request.getParameter("status") : "all";

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

        List<SpendingPlan> plansList = FinancialDAO.getSpendingPlans(clubID, keyword, status, page, pageSize);
        int totalRecords = FinancialDAO.getTotalSpendingPlanRecords(clubID, keyword, status);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        List<Events> eventsList = FinancialDAO.getEventsByClubId(clubID, term.getTermID());
        request.setAttribute("eventsList", eventsList);
        request.setAttribute("plansList", plansList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("term", term);
        request.getRequestDispatcher("/view/student/department-leader/financial-spending-plan.jsp").forward(request, response);
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

        String clubIDParam = request.getParameter("clubID");
        Integer sessionClubID = (Integer) request.getSession().getAttribute("clubID");
        int clubID;
        if (clubIDParam != null && !clubIDParam.isEmpty()) {
            try {
                clubID = Integer.parseInt(clubIDParam);
                request.getSession().setAttribute("clubID", clubID);
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

            case "addPlan":
                try {
                    SpendingPlan plan = new SpendingPlan();
                    plan.setClubID(clubID);
                    plan.setPlanName(request.getParameter("planName"));
                    plan.setTotalPlannedBudget(new BigDecimal(request.getParameter("totalPlannedBudget")));
                    String eventIDStr = request.getParameter("eventID");
                    if (eventIDStr != null && !eventIDStr.isEmpty()) {
                        plan.setEventID(Integer.parseInt(eventIDStr));
                    }
                    plan.setStatus("Chưa bắt đầu");

                    if (FinancialDAO.addSpendingPlan(plan)) {
                        // Add items if provided
                        String[] categories = request.getParameterValues("category[]");
                        String[] plannedAmounts = request.getParameterValues("plannedAmount[]");
                        String[] descriptions = request.getParameterValues("description[]");
                        boolean itemsAdded = true;
                        if (categories != null && plannedAmounts != null && categories.length == plannedAmounts.length) {
                            for (int i = 0; i < categories.length; i++) {
                                if (categories[i] == null || categories[i].trim().isEmpty() || plannedAmounts[i] == null || plannedAmounts[i].trim().isEmpty()) {
                                    continue; // Skip empty items
                                }
                                try {
                                    SpendingPlanItem item = new SpendingPlanItem();
                                    item.setPlanID(plan.getPlanID());
                                    item.setCategory(categories[i]);
                                    item.setPlannedAmount(new BigDecimal(plannedAmounts[i]));
                                    item.setDescription(descriptions != null && i < descriptions.length ? descriptions[i] : null);
                                    if (!FinancialDAO.addSpendingPlanItem(item)) {
                                        itemsAdded = false;
                                    }
                                } catch (NumberFormatException ex) {
                                    itemsAdded = false;
                                }
                            }
                        }
                        if (itemsAdded) {
                            request.setAttribute("message", "Thêm kế hoạch chi tiêu thành công!");
                        } else {
                            request.setAttribute("error", "Thêm kế hoạch thành công nhưng có lỗi khi thêm hạng mục!");
                        }
                    } else {
                        request.setAttribute("error", "Không thể thêm kế hoạch chi tiêu!");
                    }
                } catch (Exception e) {
                    request.setAttribute("error", "Lỗi khi thêm kế hoạch chi tiêu: " + e.getMessage());
                }
                break;

            case "updateStatus":
                try {
                    int planID = Integer.parseInt(request.getParameter("planID"));
                    String newStatus = request.getParameter("newStatus");
                    if (FinancialDAO.updateSpendingPlanStatus(planID, newStatus)) {
                        request.setAttribute("message", "Cập nhật trạng thái thành công!");
                    } else {
                        request.setAttribute("error", "Không thể cập nhật trạng thái!");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "ID kế hoạch không hợp lệ!");
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
        return "Servlet for managing spending plans";
    }
}
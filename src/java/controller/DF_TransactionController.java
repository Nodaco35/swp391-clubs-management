/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.DepartmentDashboardDAO;
import dal.FinancialDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import models.Term;
import models.Transaction;
import models.Users;

public class DF_TransactionController extends HttpServlet {

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

        int clubID = (int) request.getSession().getAttribute("clubID");

        Term term = (Term) request.getSession().getAttribute("term");
        if (term.getTermID() == null) {
            request.setAttribute("error", "Hiện chưa có kì nào hoạt động nên không cần quản lý tài chính");
            request.getRequestDispatcher("/myclub").forward(request, response);
            return;
        }

        if (!dashboardDAO.isDepartmentLeaderIndoingoai(user.getUserID(), clubID)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        String type = request.getParameter("type");
        String status = request.getParameter("status");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 7;

        List<Transaction> transactions = FinancialDAO.getTransactionsByClubAndTerm(clubID, term.getTermID(), type, status, page, pageSize);
        int totalTransactions = FinancialDAO.getTotalTransactions(clubID, term.getTermID(), type, status);
        int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);

        request.setAttribute("transactions", transactions);
        request.setAttribute("CREATE DATABASE demo_mvc\n" +
                "    CHARACTER SET utf8mb4\n" +
                "    COLLATE utf8mb4_unicode_ci;\n" +
                "\n" +
                "USE demo_mvc;\n" +
                "-- Bảng Roles (vai trò người dùng)\n" +
                "CREATE TABLE Roles (\n" +
                "    RoleId INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    RoleName VARCHAR(50) NOT NULL\n" +
                ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n" +
                "\n" +
                "-- Bảng Users (người dùng)\n" +
                "CREATE TABLE Users (\n" +
                "    UserId INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    UserName VARCHAR(100) NOT NULL,\n" +
                "    Email VARCHAR(100) NOT NULL UNIQUE,\n" +
                "    PasswordHash VARCHAR(255) NOT NULL,\n" +
                "    RoleId INT,\n" +
                "    FOREIGN KEY (RoleId) REFERENCES Roles(RoleId)\n" +
                ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n" +
                "\n" +
                "CREATE TABLE Logs (\n" +
                "    LogId INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    UserId INT,                      \n" +
                "    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, \n" +
                "    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE SET NULL\n" +
                ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n" +
                "-- Thêm dữ liệu vào Roles\n" +
                "INSERT INTO Roles (RoleName) VALUES\n" +
                "('Quản trị viên'),\n" +
                "('Thành viên');\n" +
                "\n" +
                "-- Thêm dữ liệu vào Users\n" +
                "INSERT INTO Users (UserName, Email, PasswordHash, RoleId) VALUES\n" +
                "('Đỗ Hữu Đức', 'duc@gmail.com', '123456', 1), -- Quản trị viên\n" +
                "('Trần Thị B', 'b@gmail.com', '123456', 2), -- Thành viên\n" +
                "('Lê Văn C', 'c@gmail.com', '123456', 2);   -- Thành viên\n" +
                "\n" +
                "DELIMITER //\n" +
                "CREATE PROCEDURE GetAllUsers()\n" +
                "BEGIN\n" +
                "    SELECT u.*, r.RoleName\n" +
                "    FROM Users u\n" +
                "    JOIN Roles r ON u.RoleId = r.RoleId;\n" +
                "END //\n" +
                "DELIMITER ;\n" +
                "\n" +
                "DELIMITER //\n" +
                "CREATE PROCEDURE LoginUser(\n" +
                "    IN p_Email VARCHAR(100),\n" +
                "    IN p_PasswordHash VARCHAR(255)\n" +
                ")\n" +
                "BEGIN\n" +
                "    DECLARE v_UserId INT;\n" +
                "\n" +
                "   \n" +
                "    SELECT u.UserId\n" +
                "    INTO v_UserId\n" +
                "    FROM Users u\n" +
                "    WHERE u.Email = p_Email AND u.PasswordHash = p_PasswordHash\n" +
                "    LIMIT 1;\n" +
                "\n" +
                "    \n" +
                "    IF v_UserId IS NOT NULL THEN\n" +
                "        \n" +
                "        INSERT INTO Logs(UserId)\n" +
                "        VALUES (v_UserId);\n" +
                "\n" +
                "        SELECT u.*, r.roleName\n" +
                "        FROM Users u\n" +
                "        JOIN Roles r ON u.RoleId = r.RoleId\n" +
                "        WHERE u.UserId = v_UserId;\n" +
                "    ELSE\n" +
                "        \n" +
                "        SELECT NULL AS UserId, NULL AS UserName, NULL AS RoleName;\n" +
                "    END IF;\n" +
                "END //\n" +
                "DELIMITER ;\n" +
                "\n" +
                "DELIMITER //\n" +
                "CREATE PROCEDURE InsertUser(\n" +
                "    IN p_UserName VARCHAR(100),\n" +
                "    IN p_Email VARCHAR(100),\n" +
                "    IN p_PasswordHash VARCHAR(255),\n" +
                "    IN p_RoleId INT\n" +
                ")\n" +
                "BEGIN\n" +
                "    INSERT INTO Users (UserName, Email, PasswordHash, RoleId)\n" +
                "    VALUES (p_UserName, p_Email, p_PasswordHash, p_RoleId);\n" +
                "\n" +
                "    -- Nếu muốn trả về ID vừa thêm:\n" +
                "    SELECT LAST_INSERT_ID() AS NewUserId;\n" +
                "END //\n" +
                "DELIMITER ;\n" +
                "\n" +
                "DELIMITER //\n" +
                "CREATE PROCEDURE UpdateUser(\n" +
                "    IN p_UserName VARCHAR(100),\n" +
                "    IN p_Email VARCHAR(100),\n" +
                "    IN p_PasswordHash VARCHAR(255),\n" +
                "    IN p_RoleId INT,\n" +
                "    IN p_UserId Int\n" +
                ")\n" +
                "BEGIN\n" +
                "    UPDATE Users\n" +
                "SET\n" +
                "\n" +
                "UserName = p_UserName,\n" +
                "Email = p_Email,\n" +
                "PasswordHash = p_PasswordHash,\n" +
                "RoleId = p_RoleId\n" +
                "WHERE UserId = p_UserId;\n" +
                "\n" +
                "SELECT ROW_COUNT() AS RowsAffected;\n" +
                "    \n" +
                "END //\n" +
                "DELIMITER ;\n" +
                "DELIMITER //\n" +
                "create procedure DeleteUser(\n" +
                "\tIN p_UserId int\n" +
                ")\n" +
                "Begin\n" +
                "\tDELETE FROM Users u\n" +
                "WHERE u.UserId = p_UserId;\n" +
                "\n" +
                "End//\n" +
                "DELIMITER ;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n", clubID);
        request.setAttribute("termID", term.getTermID());
        request.setAttribute("type", type);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/view/student/department-leader/financial-transaction-club-history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DF_TransactionController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DF_TransactionController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

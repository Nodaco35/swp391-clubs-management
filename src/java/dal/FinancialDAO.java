/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.math.BigDecimal;
import models.*;
import java.util.*;
import java.sql.*;
/**
 *
 * @author he181
 */
public class FinancialDAO {

    public static BigDecimal getTotalIncomeAmount(int clubID, String termID) {
        BigDecimal getTotalAmount = BigDecimal.ZERO;
        String sql = "SELECT SUM(amount) AS total\n"
                + "FROM income\n"
                + "WHERE ClubID = ? AND TermID = ? and status = 'Đã nhận'";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ps.setObject(2, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                getTotalAmount = rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            return BigDecimal.ZERO;
        }
        return getTotalAmount;
    }

    public static String getPreviousTermID(String currentTermID) {
        if (currentTermID == null || currentTermID.isEmpty()) {
            return null;
        }

        String[] terms = {"FA", "SP", "SU"};
        String season = currentTermID.substring(0, 2); // e.g., "SU"
        String yearStr = currentTermID.substring(2);   // e.g., "25"
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            return null; // Invalid TermID format
        }

        if (season.equalsIgnoreCase("SU")) {
            return "SP" + yearStr; // SU25 -> SP25
        } else if (season.equalsIgnoreCase("SP")) {
            return "FA" + (year - 1); // SP25 -> FA24
        } else if (season.equalsIgnoreCase("FA")) {
            return "SU" + (year - 1); // FA24 -> SU24
        } else {
            return null; // Unknown season
        }
    }

    public static BigDecimal getTotalExpenseAmount(int clubID, String termID) {
        BigDecimal getTotalAmount = BigDecimal.ZERO;
        String sql = """
                     SELECT SUM(amount) AS total
                                     FROM expenses
                                     WHERE ClubID = ? AND TermID = ? and approved = 1;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ps.setObject(2, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                getTotalAmount = rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            return BigDecimal.ZERO;
        }
        return getTotalAmount;
    }

    public static BigDecimal getTotalIncomeMemberPending(int clubID, String termID) {
        BigDecimal getTotalAmount = BigDecimal.ZERO;
        String sql = """
                     SELECT 
                         
                         SUM(mic.Amount) AS TotalPendingAmount
                     FROM MemberIncomeContributions mic
                     WHERE mic.TermID = ?
                     AND mic.ClubID = ?
                     AND mic.ContributionStatus = 'Pending';""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(2, clubID);
            ps.setObject(1, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                getTotalAmount = rs.getBigDecimal("TotalPendingAmount");
            }
        } catch (SQLException e) {
            return BigDecimal.ZERO;
        }
        return getTotalAmount;
    }

    public static int getTotalIncomePersonByType(int clubID, String termID, String type) {
        int getTotalAmount = 0;
        String sql = """
                     SELECT 
                          COUNT(mic.UserID) AS UniquePendingMembers
                         
                     FROM MemberIncomeContributions mic
                     WHERE mic.TermID = ?
                     AND mic.ClubID = ?
                     AND mic.ContributionStatus = ?; """;
        if (type == "") {
            sql = """
                     SELECT 
                          COUNT( mic.UserID) AS UniquePendingMembers
                         
                     FROM MemberIncomeContributions mic
                     WHERE mic.TermID = ?
                     AND mic.ClubID = ?
                      """;
        }

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(2, clubID);
            ps.setObject(1, termID);
            if (type != "") {
                ps.setString(3, type);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                getTotalAmount = rs.getInt("UniquePendingMembers");
            }
        } catch (SQLException e) {
            return 0;
        }
        return getTotalAmount;
    }

    public static List<MemberIncomeContributions> getPreviewIncomeMemberSrc(int clubID, String termID) {
        List<MemberIncomeContributions> getPreviewIncomeMemberSrc = new ArrayList<>();
        String sql = """
                     SELECT mic.*, u.FullName, u.Email, u.AvatarSrc FROM memberincomecontributions mic
                     
                     join users u on mic.UserID = u.UserID 
                     Where mic.ClubID = ? and mic.TermID = ?
                     limit 2
                     ;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ps.setObject(2, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MemberIncomeContributions mic = new MemberIncomeContributions();
                mic.setContributionID(rs.getInt("ContributionID"));
                mic.setIncomeID(rs.getInt("IncomeID"));
                mic.setUserID(rs.getString("UserID"));
                mic.setClubID(rs.getInt("ClubID"));
                mic.setTermID(rs.getString("TermID"));
                mic.setAmount(rs.getBigDecimal("Amount"));
                mic.setContributionStatus(rs.getString("ContributionStatus"));
                mic.setPaidDate(rs.getTimestamp("PaidDate"));
                mic.setCreatedAt(rs.getTimestamp("CreatedAt"));
                mic.setUserName(rs.getString("FullName"));
                mic.setEmail(rs.getString("Email"));
                mic.setAvtSrc(rs.getString("AvatarSrc"));
                mic.setDueDate(rs.getTimestamp("DueDate"));
                getPreviewIncomeMemberSrc.add(mic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getPreviewIncomeMemberSrc.isEmpty() ? null : getPreviewIncomeMemberSrc;
    }

    public static List<Transaction> getRecentTransactions(int clubID, String termID) {
        List<Transaction> getRecentTransactions = new ArrayList<>();
        String sql = """
                    SELECT t.*, u.FullName FROM transactions t
                                     join users u on t.CreatedBy = u.UserID
                                          where t.Status = 'Approved' and ClubID = ? and  TermID = ? 
                                          order by CreatedAt desc 
                     limit 4;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ps.setObject(2, termID);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionID(rs.getInt("TransactionID"));
                t.setClubID(rs.getInt("ClubID"));
                t.setTermID(rs.getString("TermID"));
                t.setType(rs.getString("Type"));
                t.setAmount(rs.getBigDecimal("Amount"));
                t.setTransactionDate(rs.getTimestamp("TransactionDate"));
                t.setDescription(rs.getString("Description"));
                t.setAttachment(rs.getString("Attachment"));
                t.setStatus(rs.getString("Status"));
                t.setReferenceID(rs.getInt("ReferenceID"));
                t.setCreatedDate(rs.getTimestamp("CreatedAt"));

                t.setCreatedName(rs.getString("FullName"));
                getRecentTransactions.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getRecentTransactions;
    }

    public static int getIncomeIDPending(int clubID, String termID) {
        int incomeIDfirst = 0;
        String sql = """
                     SELECT *
                     FROM income
                     WHERE ClubID = ? AND TermID = ? and Source = 'Phí thành viên' and status = 'Đang chờ'
                     limit 1""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ps.setObject(2, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                incomeIDfirst = rs.getInt("IncomeID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomeIDfirst;
    }

    public static List<MemberIncomeContributions> IncomeMemberSrc(int incomeID, String keyword, String status, int page, int pageSize) {
        List<MemberIncomeContributions> list = new ArrayList<>();
        String sql = """
                     SELECT mic.*, u.FullName, u.Email, u.AvatarSrc 
                     FROM memberincomecontributions mic
                     JOIN users u ON mic.UserID = u.UserID
                     WHERE mic.IncomeID = ? AND u.Status = 1""";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (u.FullName LIKE ? OR u.Email LIKE ?)";
        }
        if (status != null && !status.equals("all")) {
            sql += " AND mic.ContributionStatus = ?";
        }
        sql += " ORDER BY mic.ContributionID LIMIT ? OFFSET ?";

        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, incomeID);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, likePattern);
                ps.setString(paramIndex++, likePattern);
            }
            if (status != null && !status.equals("all")) {
                ps.setString(paramIndex++, status);
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, (page - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MemberIncomeContributions mic = new MemberIncomeContributions();
                mic.setContributionID(rs.getInt("ContributionID"));
                mic.setIncomeID(rs.getInt("IncomeID"));
                mic.setUserID(rs.getString("UserID"));
                mic.setAvtSrc(rs.getString("AvatarSrc"));
                mic.setUserName(rs.getString("FullName"));
                mic.setEmail(rs.getString("Email"));
                mic.setContributionStatus(rs.getString("ContributionStatus"));
                mic.setAmount(rs.getBigDecimal("Amount"));
                mic.setPaidDate(rs.getTimestamp("PaidDate"));
                mic.setDueDate(rs.getTimestamp("DueDate"));
                list.add(mic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getTotalIncomeMemberRecords(int incomeID, String keyword, String status) {
        int totalRecords = 0;
        String sql = """
                     SELECT COUNT(*) AS total 
                     FROM memberincomecontributions mic
                     JOIN users u ON mic.UserID = u.UserID
                     WHERE mic.IncomeID = ? AND u.Status = 1""";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (u.FullName LIKE ? OR u.Email LIKE ?)";
        }
        if (status != null && !status.equals("all")) {
            sql += " AND mic.ContributionStatus = ?";
        }

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            int paramIndex = 1;
            ps.setInt(paramIndex++, incomeID);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, likePattern);
                ps.setString(paramIndex++, likePattern);
            }
            if (status != null && !status.equals("all")) {
                ps.setString(paramIndex++, status);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalRecords = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRecords;
    }

    public static List<Income> getIncomeMemberPendings(int clubID, String termID) {
        String sql = """
                     SELECT *
                                          FROM income
                                          WHERE ClubID = ? AND TermID = ? and Source = 'Phí thành viên' and status = 'Đang chờ'
                                          """;
        List<Income> getIncomeMemberPendings = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ps.setObject(2, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Income i = new Income();
                i.setIncomeID(rs.getInt("IncomeID"));
                i.setDescription(rs.getString("Description"));
                getIncomeMemberPendings.add(i);
            }
        } catch (Exception e) {
        }
        return getIncomeMemberPendings;
    }

    public static boolean markContributionPaid(int contributionID) {
        String sql = """
                     UPDATE memberincomecontributions 
                     SET ContributionStatus = 'Paid', PaidDate = CURRENT_TIMESTAMP 
                     WHERE ContributionID = ? AND ContributionStatus = 'Pending'""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, contributionID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean remindMember(int contributionID, String email) {
        // Giả lập gửi email nhắc nhở (thay bằng logic thực tế nếu có hệ thống email)
        System.out.println("Sending reminder to: " + email + " for ContributionID: " + contributionID);
        // Ví dụ: Gửi email qua API hoặc thư viện như JavaMail
        return true; // Trả về true giả lập thành công
    }

    public static boolean remindAllPending(int incomeID) {
        String sql = """
                     SELECT u.Email 
                     FROM memberincomecontributions mic
                     JOIN users u ON mic.UserID = u.UserID
                     WHERE mic.IncomeID = ? AND mic.ContributionStatus = 'Pending' AND u.Status = 1""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, incomeID);
            ResultSet rs = ps.executeQuery();
            boolean allSuccess = true;
            while (rs.next()) {
                String email = rs.getString("Email");
                // Giả lập gửi email nhắc nhở
                System.out.println("Sending reminder to: " + email);
                // Thay bằng logic gửi email thực tế
                allSuccess = allSuccess && true; // Giả lập thành công
            }
            return allSuccess;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean completeIncome(int incomeID, String termID, int clubID, Income income, String userID) {
        String sql = """
                     UPDATE income 
                     SET status = 'Đã nhận' 
                     WHERE IncomeID = ? AND status = 'Đang chờ'""";
        String sql2 = """
                      INSERT INTO Transactions (ClubID, TermID, Type, Amount, TransactionDate, Description, Attachment, Status, ReferenceID, CreatedBy)
                      VALUES
                      (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            PreparedStatement ps2 = DBContext.getConnection().prepareStatement(sql2);
            ps.setInt(1, incomeID);
            ps2.setObject(1, clubID);
            ps2.setObject(2, termID);
            ps2.setObject(3, "Income");
            ps2.setObject(4, income.getAmount());
            ps2.setObject(5, new Timestamp(System.currentTimeMillis()));
            ps2.setObject(6, income.getDescription());
            ps2.setObject(7, income.getAttachment());
            ps2.setObject(8, "Approved");
            ps2.setObject(9, incomeID);
            ps2.setObject(10, userID);
            int rowsAffected = ps.executeUpdate();
            int row = ps2.executeUpdate();
            
            return (row > 0 && rowsAffected > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }

    }

    public static boolean areAllContributionsPaid(int incomeID) {
        String sql = """
                     SELECT COUNT(*) AS pendingCount 
                     FROM memberincomecontributions 
                     WHERE IncomeID = ? AND ContributionStatus = 'Pending'""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, incomeID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("pendingCount") == 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasPendingContributions(int incomeID) {
        String sql = """
                     SELECT COUNT(*) AS pendingCount 
                     FROM memberincomecontributions 
                     WHERE IncomeID = ? AND ContributionStatus = 'Pending'""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, incomeID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("pendingCount") > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<MemberIncomeContributions> getUserUnpaidInComeIDs(int incomeID) {
        String sql = """
                     SELECT *
                                          FROM MemberIncomeContributions mic
                                          Join users  u on mic.UserID = u.UserID
                                          WHERE 
                                          mic.ContributionStatus = 'Pending' and mic.incomeID = ?""";
        List<MemberIncomeContributions> l = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, incomeID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MemberIncomeContributions mic = new MemberIncomeContributions();
                mic.setContributionID(rs.getInt("ContributionID"));
                mic.setIncomeID(rs.getInt("IncomeID"));
                mic.setUserID(rs.getString("UserID"));
                mic.setAvtSrc(rs.getString("AvatarSrc"));
                mic.setUserName(rs.getString("FullName"));
                mic.setEmail(rs.getString("Email"));
                mic.setContributionStatus(rs.getString("ContributionStatus"));
                mic.setAmount(rs.getBigDecimal("Amount"));
                mic.setPaidDate(rs.getTimestamp("PaidDate"));
                mic.setDueDate(rs.getTimestamp("DueDate"));
                l.add(mic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return l;
    }

    public static Income findByIncomeID(int incomeID) {
        String sql = """
                     SELECT *
                                          FROM Income
                                          
                                          WHERE 
                                          IncomeID = ?""";
        List<Income> l = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, incomeID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Income i = new Income();
                i.setIncomeID(rs.getInt("IncomeID"));
                i.setClubID(rs.getInt("ClubID"));
                i.setTermID(rs.getString("TermID"));
                i.setSource(rs.getString("Source"));
                i.setAmount(rs.getBigDecimal("Amount"));
                i.setIncomeDate(rs.getTimestamp("IncomeDate"));
                i.setDescription(rs.getString("Description"));
                i.setAttachment(rs.getString("Attachment"));
                i.setStatus(rs.getString("Status"));
                l.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return l.get(0);
    }

    public static List<Expenses> getExpenses(int clubID, String termID, String keyword, String status, int page, int pageSize) {
        List<Expenses> expensesList = new ArrayList<>();
        String sql = """
                     SELECT e.*, u.FullName AS CreatedByName
                     FROM Expenses e
                     LEFT JOIN Transactions t ON e.ExpenseID = t.ReferenceID AND t.Type = 'Expense'
                     LEFT JOIN Users u ON t.CreatedBy = u.UserID
                     WHERE e.ClubID = ? AND e.TermID = ?""";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (e.Purpose LIKE ? OR e.Description LIKE ?)";
        }
        if (status != null && !status.equals("all")) {
            boolean approved = status.equals("Approved");
            sql += " AND e.Approved = ?";
        }
        sql += " ORDER BY e.ExpenseDate DESC LIMIT ? OFFSET ?";

        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, clubID);
            ps.setString(paramIndex++, termID);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, likePattern);
                ps.setString(paramIndex++, likePattern);
            }
            if (status != null && !status.equals("all")) {
                ps.setBoolean(paramIndex++, status.equals("Approved"));
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, (page - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Expenses expense = new Expenses();
                expense.setExpenseID(rs.getInt("ExpenseID"));
                expense.setClubID(rs.getInt("ClubID"));
                expense.setTermID(rs.getString("TermID"));
                expense.setPurpose(rs.getString("Purpose"));
                expense.setAmount(rs.getBigDecimal("Amount"));
                expense.setExpenseDate(rs.getDate("ExpenseDate"));
                expense.setDescription(rs.getString("Description"));
                expense.setAttachment(rs.getString("Attachment"));
                expense.setApproved(rs.getBoolean("Approved"));
                expensesList.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expensesList;
    }

    /**
     * Retrieves the total number of expense records for pagination.
     */
    public static int getTotalExpenseRecords(int clubID, String termID, String keyword, String status) {
        int totalRecords = 0;
        String sql = """
                     SELECT COUNT(*) AS total
                     FROM Expenses e
                     WHERE e.ClubID = ? AND e.TermID = ?""";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (e.Purpose LIKE ? OR e.Description LIKE ?)";
        }
        if (status != null && !status.equals("all")) {
            boolean approved = status.equals("Approved");
            sql += " AND e.Approved = ?";
        }

        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, clubID);
            ps.setString(paramIndex++, termID);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, likePattern);
                ps.setString(paramIndex++, likePattern);
            }
            if (status != null && !status.equals("all")) {
                ps.setBoolean(paramIndex++, status.equals("Approved"));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalRecords = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRecords;
    }

    /**
     * Adds a new expense and creates a corresponding transaction.
     */
    public static boolean addExpense(Expenses expense, String userID) {
        String expenseSql = """
                           INSERT INTO Expenses (ClubID, TermID, Purpose, Amount, ExpenseDate, Description, Attachment, Approved)
                           VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";
        String transactionSql = """
                              INSERT INTO Transactions (ClubID, TermID, Type, Amount, TransactionDate, Description, Attachment, Status, ReferenceID, CreatedBy, CreatedAt)
                              VALUES (?, ?, 'Expense', ?, ?, ?, ?, ?, ?, ?, ?)""";
        Connection conn = null;
        PreparedStatement psExpense = null;
        PreparedStatement psTransaction = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert into Expenses
            psExpense = conn.prepareStatement(expenseSql, Statement.RETURN_GENERATED_KEYS);
            psExpense.setInt(1, expense.getClubID());
            psExpense.setString(2, expense.getTermID());
            psExpense.setString(3, expense.getPurpose());
            psExpense.setBigDecimal(4, expense.getAmount());
            psExpense.setDate(5, new java.sql.Date(expense.getExpenseDate().getTime()));// Changed to setDate
            psExpense.setString(6, expense.getDescription());
            psExpense.setString(7, expense.getAttachment());
            psExpense.setBoolean(8, false); // Default: not approved
            int rowsAffected = psExpense.executeUpdate();

            // Get generated ExpenseID
            rs = psExpense.getGeneratedKeys();
            int expenseID = 0;
            if (rs.next()) {
                expenseID = rs.getInt(1);
            }

            // Insert into Transactions
            psTransaction = conn.prepareStatement(transactionSql);
            psTransaction.setInt(1, expense.getClubID());
            psTransaction.setString(2, expense.getTermID());
            psTransaction.setBigDecimal(3, expense.getAmount());
            psTransaction.setDate(4, new java.sql.Date(expense.getExpenseDate().getTime())); // Changed to setDate for consistency
            psTransaction.setString(5, expense.getDescription());
            psTransaction.setString(6, expense.getAttachment());
            psTransaction.setString(7, "Pending"); // Default status
            psTransaction.setInt(8, expenseID);
            psTransaction.setString(9, userID);
            psTransaction.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            psTransaction.executeUpdate();

            conn.commit(); // Commit transaction
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (psExpense != null) psExpense.close();
                if (psTransaction != null) psTransaction.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Approves an expense and updates the corresponding transaction.
     */
    public static boolean approveExpense(int expenseID, String userID) {
        String expenseSql = """
                           UPDATE Expenses 
                           SET Approved = TRUE 
                           WHERE ExpenseID = ? AND Approved = FALSE""";
        String transactionSql = """
                              UPDATE Transactions 
                              SET Status = 'Approved' 
                              WHERE ReferenceID = ? AND Type = 'Expense' AND Status = 'Pending'""";
        Connection conn = null;
        PreparedStatement psExpense = null;
        PreparedStatement psTransaction = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Update Expenses
            psExpense = conn.prepareStatement(expenseSql);
            psExpense.setInt(1, expenseID);
            int rowsAffected = psExpense.executeUpdate();

            // Update Transactions
            psTransaction = conn.prepareStatement(transactionSql);
            psTransaction.setInt(1, expenseID);
            psTransaction.executeUpdate();

            conn.commit(); // Commit transaction
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (psExpense != null) psExpense.close();
                if (psTransaction != null) psTransaction.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static Expenses getExpenseByID(int expenseID) {
        String sql = "SELECT * FROM Expenses WHERE ExpenseID = ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, expenseID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Expenses e = new Expenses();
                e.setExpenseID(rs.getInt("ExpenseID"));
                e.setClubID(rs.getInt("ClubID"));
                e.setTermID(rs.getString("TermID"));
                e.setPurpose(rs.getString("Purpose"));
                e.setAmount(rs.getBigDecimal("Amount"));
                e.setExpenseDate(rs.getDate("ExpenseDate"));
                e.setDescription(rs.getString("Description"));
                e.setAttachment(rs.getString("Attachment"));
                e.setApproved(rs.getBoolean("Approved"));
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
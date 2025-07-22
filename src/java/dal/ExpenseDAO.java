package dal;

import static dal.DBContext.getConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Expenses;
import models.Transaction;

public class ExpenseDAO {

    public boolean submitExpense(Expenses expense, String userID) {
        String sql = """
                     INSERT INTO Expenses (ClubID, TermID, Purpose, Amount, ExpenseDate, Description, Attachment, CreatedBy, Status)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                     """;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expense.getClubID());
            ps.setString(2, expense.getTermID());
            ps.setString(3, expense.getPurpose());
            ps.setBigDecimal(4, expense.getAmount());
            ps.setTimestamp(5, new Timestamp(expense.getExpenseDate().getTime()));
            ps.setString(6, expense.getDescription());
            ps.setString(7, expense.getAttachment());
            ps.setString(8, userID);
            ps.setString(9, "Pending");
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Expenses> getUserSubmittedExpenses(String userID, int clubID, String termID, int page, int pageSize, String search, String status, String sortBy) {
        List<Expenses> expenses = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT e.*, u.FullName AS CreatedByName 
                FROM Expenses e
                JOIN Users u ON e.CreatedBy = u.UserID
                WHERE e.CreatedBy = ? AND e.ClubID = ? AND e.TermID = ?
                """);
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (e.Description LIKE ? OR e.Purpose LIKE ? OR CAST(e.Amount AS VARCHAR) LIKE ?)");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND e.Status = ?");
        }
        // Handle sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String[] sortParts = sortBy.split("-");
            String sortField = sortParts[0];
            String sortDirection = sortParts.length > 1 ? sortParts[1].toUpperCase() : "DESC";
            switch (sortField) {
                case "description":
                    sql.append(" ORDER BY e.Description ").append(sortDirection);
                    break;
                case "amount":
                    sql.append(" ORDER BY e.Amount ").append(sortDirection);
                    break;
                case "expenseDate":
                    sql.append(" ORDER BY e.ExpenseDate ").append(sortDirection);
                    break;
                case "purpose":
                    sql.append(" ORDER BY e.Purpose ").append(sortDirection);
                    break;
                default:
                    sql.append(" ORDER BY e.CreatedAt DESC");
                    break;
            }
        } else {
            sql.append(" ORDER BY e.CreatedAt DESC");
        }
        sql.append(" Limit ? OFFSET ? ");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setString(paramIndex++, userID);
            ps.setInt(paramIndex++, clubID);
            ps.setString(paramIndex++, termID);
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex++, page);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Expenses expense = new Expenses();
                expense.setExpenseID(rs.getInt("ExpenseID"));
                expense.setClubID(rs.getInt("ClubID"));
                expense.setTermID(rs.getString("TermID"));
                expense.setPurpose(rs.getString("Purpose"));
                expense.setAmount(rs.getBigDecimal("Amount"));
                expense.setExpenseDate(rs.getTimestamp("ExpenseDate"));
                expense.setDescription(rs.getString("Description"));
                expense.setAttachment(rs.getString("Attachment"));
                expense.setCreatedBy(rs.getString("CreatedBy"));
                expense.setStatus(rs.getString("Status"));
                expense.setCreatedAt(rs.getTimestamp("CreatedAt"));
                expense.setApprovedBy(rs.getString("ApprovedBy"));
                expense.setApprovedAt(rs.getTimestamp("ApprovedAt"));
                expense.setCreatedByName(rs.getString("CreatedByName"));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public int getUserSubmittedExpensesCount(String userID, int clubID, String termID, String search, String status) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) 
                FROM Expenses e
                WHERE e.CreatedBy = ? AND e.ClubID = ? AND e.TermID = ?
                """);
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (e.Description LIKE ? OR e.Purpose LIKE ? OR CAST(e.Amount AS VARCHAR) LIKE ?)");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND e.Status = ?");
        }

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setString(paramIndex++, userID);
            ps.setInt(paramIndex++, clubID);
            ps.setString(paramIndex++, termID);
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Expenses> getExpenses(int clubID, String termID, int page, int pageSize, String search, String status, String sortBy) {
        List<Expenses> expenses = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT e.*, u.FullName AS CreatedByName 
                FROM Expenses e
                JOIN Users u ON e.CreatedBy = u.UserID
                WHERE e.ClubID = ? AND e.TermID = ?
                """);
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (e.Description LIKE ? OR e.Purpose LIKE ? OR CAST(e.Amount AS VARCHAR) LIKE ? OR u.FullName LIKE ?)");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND e.Status = ?");
        }
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String[] sortParts = sortBy.split("-");
            String sortField = sortParts[0];
            String sortDirection = sortParts.length > 1 ? sortParts[1].toUpperCase() : "DESC";
            switch (sortField) {
                case "description":
                    sql.append(" ORDER BY e.Description ").append(sortDirection);
                    break;
                case "amount":
                    sql.append(" ORDER BY e.Amount ").append(sortDirection);
                    break;
                case "expenseDate":
                    sql.append(" ORDER BY e.ExpenseDate ").append(sortDirection);
                    break;
                case "purpose":
                    sql.append(" ORDER BY e.Purpose ").append(sortDirection);
                    break;
                default:
                    sql.append(" ORDER BY e.CreatedAt DESC");
                    break;
            }
        } else {
            sql.append(" ORDER BY e.CreatedAt DESC");
        }
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, clubID);
            ps.setString(paramIndex++, termID);
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Expenses expense = new Expenses();
                expense.setExpenseID(rs.getInt("ExpenseID"));
                expense.setClubID(rs.getInt("ClubID"));
                expense.setTermID(rs.getString("TermID"));
                expense.setPurpose(rs.getString("Purpose"));
                expense.setAmount(rs.getBigDecimal("Amount"));
                expense.setExpenseDate(rs.getTimestamp("ExpenseDate"));
                expense.setDescription(rs.getString("Description"));
                expense.setAttachment(rs.getString("Attachment"));
                expense.setCreatedBy(rs.getString("CreatedBy"));
                expense.setStatus(rs.getString("Status"));
                expense.setCreatedAt(rs.getTimestamp("CreatedAt"));
                expense.setApprovedBy(rs.getString("ApprovedBy"));
                expense.setApprovedAt(rs.getTimestamp("ApprovedAt"));
                expense.setCreatedByName(rs.getString("CreatedByName"));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public int getExpensesCount(int clubID, String termID, String search, String status) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) 
                FROM Expenses e
                JOIN Users u ON e.CreatedBy = u.UserID
                WHERE e.ClubID = ? AND e.TermID = ?
                """);
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (e.Description LIKE ? OR e.Purpose LIKE ? OR CAST(e.Amount AS VARCHAR) LIKE ? OR u.FullName LIKE ?)");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND e.Status = ?");
        }

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, clubID);
            ps.setString(paramIndex++, termID);
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Expenses> getPendingExpenses(int clubID, String termID) {
        List<Expenses> expenses = new ArrayList<>();
        String sql = """
                     SELECT e.*, u.FullName AS CreatedByName 
                     FROM Expenses e
                     JOIN Users u ON e.CreatedBy = u.UserID
                     WHERE e.ClubID = ? AND e.TermID = ? AND e.Status = 'Pending'
                     ORDER BY e.CreatedAt DESC
                     """;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubID);
            ps.setString(2, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Expenses expense = new Expenses();
                expense.setExpenseID(rs.getInt("ExpenseID"));
                expense.setClubID(rs.getInt("ClubID"));
                expense.setTermID(rs.getString("TermID"));
                expense.setPurpose(rs.getString("Purpose"));
                expense.setAmount(rs.getBigDecimal("Amount"));
                expense.setExpenseDate(rs.getTimestamp("ExpenseDate"));
                expense.setDescription(rs.getString("Description"));
                expense.setAttachment(rs.getString("Attachment"));
                expense.setCreatedBy(rs.getString("CreatedBy"));
                expense.setStatus(rs.getString("Status"));
                expense.setCreatedAt(rs.getTimestamp("CreatedAt"));
                expense.setApprovedBy(rs.getString("ApprovedBy"));
                expense.setApprovedAt(rs.getTimestamp("ApprovedAt"));
                expense.setCreatedByName(rs.getString("CreatedByName"));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public List<Expenses> getUserSubmittedExpenses(String userID, int clubID, String termID) {
        List<Expenses> expenses = new ArrayList<>();
        String sql = """
                     SELECT e.*, u.FullName AS CreatedByName 
                     FROM Expenses e
                     JOIN Users u ON e.CreatedBy = u.UserID
                     WHERE e.CreatedBy = ? AND e.ClubID = ? AND e.TermID = ?
                     ORDER BY e.CreatedAt DESC
                     """;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.setInt(2, clubID);
            ps.setString(3, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Expenses expense = new Expenses();
                expense.setExpenseID(rs.getInt("ExpenseID"));
                expense.setClubID(rs.getInt("ClubID"));
                expense.setTermID(rs.getString("TermID"));
                expense.setPurpose(rs.getString("Purpose"));
                expense.setAmount(rs.getBigDecimal("Amount"));
                expense.setExpenseDate(rs.getTimestamp("ExpenseDate"));
                expense.setDescription(rs.getString("Description"));
                expense.setAttachment(rs.getString("Attachment"));
                expense.setCreatedBy(rs.getString("CreatedBy"));
                expense.setStatus(rs.getString("Status"));
                expense.setCreatedAt(rs.getTimestamp("CreatedAt"));
                expense.setApprovedBy(rs.getString("ApprovedBy"));
                expense.setApprovedAt(rs.getTimestamp("ApprovedAt"));
                expense.setCreatedByName(rs.getString("CreatedByName"));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public boolean updateExpenseStatus(int expenseID, String status, String approvedBy) {
        String sql = """
                     UPDATE Expenses 
                     SET Status = ?, ApprovedBy = ?, ApprovedAt = CURRENT_TIMESTAMP
                     WHERE ExpenseID = ? AND Status = 'Pending'
                     """;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, approvedBy);
            ps.setInt(3, expenseID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addTransactionForApprovedExpense(Expenses expense, String userID) {
        String sql = """
                     INSERT INTO Transactions (ClubID, TermID, Type, Amount, TransactionDate, Description, Attachment, CreatedBy, Status, ReferenceID)
                     VALUES (?, ?, 'Expense', ?, ?, ?, ?, ?, 'Approved', ?)
                     """;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expense.getClubID());
            ps.setString(2, expense.getTermID());
            ps.setBigDecimal(3, expense.getAmount());
            ps.setTimestamp(4, new Timestamp(expense.getExpenseDate().getTime()));
            ps.setString(5, expense.getDescription());
            ps.setString(6, expense.getAttachment());
            ps.setString(7, userID);
            ps.setInt(8, expense.getExpenseID());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // chairman
    
    // Get completed expense transactions with search, termID, and pagination
    public List<Transaction> getCompletedTransactions(int clubID, String search, String termID, int page, int pageSize) {
        List<Transaction> transactions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT expenseID, clubID, termID, purpose, amount, expenseDate, description, attachment, status, createdBy, createdByName " +
            "FROM Expenses " +
            "WHERE clubID = ? AND status = 'APPROVED'"
        );

        // Add search condition
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND description LIKE ?");
        }

        // Add termID condition
        if (termID != null && !termID.equals("all")) {
            sql.append(" AND termID = ?");
        }

        // Add pagination
        sql.append(" ORDER BY expenseDate DESC");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, clubID);

            if (search != null && !search.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + search + "%");
            }

            if (termID != null && !termID.equals("all")) {
                stmt.setString(paramIndex++, termID);
            }

            stmt.setInt(paramIndex++, (page - 1) * pageSize);
            stmt.setInt(paramIndex, pageSize);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionID(rs.getInt("expenseID"));
                transaction.setClubID(rs.getInt("clubID"));
                transaction.setTermID(rs.getString("termID"));
                transaction.setType("Expense");
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setTransactionDate(rs.getTimestamp("expenseDate"));
                transaction.setDescription(rs.getString("description"));
                transaction.setAttachment(rs.getString("attachment"));
                transaction.setStatus(rs.getString("status"));
                transaction.setCategory(rs.getString("purpose")); // Map purpose to category
                transaction.setCreateBy(rs.getString("createdBy"));
                transaction.setCreatedName(rs.getString("createdByName"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving completed expense transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Count completed expense transactions for pagination
    public int countCompletedTransactions(int clubID, String search, String termID) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) " +
            "FROM Expenses " +
            "WHERE clubID = ? AND status = 'APPROVED'"
        );

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND description LIKE ?");
        }

        if (termID != null && !termID.equals("all")) {
            sql.append(" AND termID = ?");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, clubID);

            if (search != null && !search.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + search + "%");
            }

            if (termID != null && !termID.equals("all")) {
                stmt.setString(paramIndex, termID);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error counting completed expense transactions: " + e.getMessage());
        }
        return 0;
    }

    // Get total expenses for a club (optionally filtered by termID)
    public double getTotalExpense(int clubID, String termID) {
        StringBuilder sql = new StringBuilder(
            "SELECT SUM(amount) " +
            "FROM Expenses " +
            "WHERE clubID = ? AND status = 'APPROVED'"
        );

        if (termID != null && !termID.equals("all")) {
            sql.append(" AND termID = ?");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setInt(1, clubID);
            if (termID != null && !termID.equals("all")) {
                stmt.setString(2, termID);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error calculating total expenses: " + e.getMessage());
        }
        return 0.0;
    }
}
package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Expenses;

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

    // Get list of pending expenses for approval
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

    // Get list of expenses submitted by a specific user
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

    // Approve or reject an expense
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

    // Add transaction after expense approval
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
}
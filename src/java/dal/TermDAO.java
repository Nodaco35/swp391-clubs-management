/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;
import java.util.*;
import models.*;
import java.sql.*;
/**
 *
 * @author he181
 */
public class TermDAO {

    public static Term getActiveSemester() {
        Term t = new Term();
        String sql = "SELECT * FROM Semesters\n"
                + "WHERE Status = 'ACTIVE'";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                t.setTermID(rs.getString("TermID"));
                t.setTermName(rs.getString("TermName"));
                t.setStartDate(rs.getDate("StartDate"));
                t.setEndDate(rs.getDate("EndDate"));
                t.setStatus(rs.getString("Status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return t;
    }
    
    public List<Term> getAllSemesters() {
        List<Term> semesters = new ArrayList<>();
        String sql = "SELECT * FROM Semesters ORDER BY StartDate DESC";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Term t = new Term();
                t.setTermID(rs.getString("TermID"));
                t.setTermName(rs.getString("TermName"));
                t.setStartDate(rs.getDate("StartDate"));
                t.setEndDate(rs.getDate("EndDate"));
                t.setStatus(rs.getString("Status"));
                semesters.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semesters;
    }

    public List<Term> getPaginatedSemesters(String search, int page, int pageSize) {
        List<Term> semesters = new ArrayList<>();
        String sql = "SELECT * FROM Semesters WHERE TermID LIKE ? OR TermName LIKE ? ORDER BY StartDate DESC LIMIT ? OFFSET ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            String searchPattern = search != null ? "%" + search.trim() + "%" : "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setInt(3, pageSize);
            ps.setInt(4, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Term t = new Term();
                t.setTermID(rs.getString("TermID"));
                t.setTermName(rs.getString("TermName"));
                t.setStartDate(rs.getDate("StartDate"));
                t.setEndDate(rs.getDate("EndDate"));
                t.setStatus(rs.getString("Status"));
                semesters.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semesters;
    }

    public int getTotalSemesters(String search) {
        String sql = "SELECT COUNT(*) FROM Semesters WHERE TermID LIKE ? OR TermName LIKE ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            String searchPattern = search != null ? "%" + search.trim() + "%" : "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean termExists(String termID) {
        String sql = "SELECT COUNT(*) FROM Semesters WHERE TermID = ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, termID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addSemester(Term term) throws SQLException {
        String sql = "INSERT INTO Semesters (TermID, TermName, StartDate, EndDate, Status) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, term.getTermID());
            ps.setString(2, term.getTermName());
            ps.setDate(3, new java.sql.Date(term.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(term.getEndDate().getTime()));
            ps.setString(5, term.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi thêm kỳ học: " + e.getMessage());
        }
    }

    public boolean updateSemester(String termID, Term term) throws SQLException {
        String sql = "UPDATE Semesters SET TermName = ?, StartDate = ?, EndDate = ?, Status = ? WHERE TermID = ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, term.getTermName());
            ps.setDate(2, new java.sql.Date(term.getStartDate().getTime()));
            ps.setDate(3, new java.sql.Date(term.getEndDate().getTime()));
            ps.setString(4, term.getStatus());
            ps.setString(5, termID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi cập nhật kỳ học: " + e.getMessage());
        }
    }

    public boolean deleteSemester(String termID) throws SQLException {
        String sql = "DELETE FROM Semesters WHERE TermID = ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, termID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi xóa kỳ học: " + e.getMessage());
        }
    }
}
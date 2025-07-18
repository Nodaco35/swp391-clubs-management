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
                          COUNT(DISTINCT mic.UserID) AS UniquePendingMembers
                         
                     FROM MemberIncomeContributions mic
                     WHERE mic.TermID = ?
                     AND mic.ClubID = ?
                     AND mic.ContributionStatus = ?; """;
        if (type == "") {
            sql = """
                     SELECT 
                          COUNT(DISTINCT mic.UserID) AS UniquePendingMembers
                         
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
}

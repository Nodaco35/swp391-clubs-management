/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import static dal.DBContext.getConnection;
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
        String sql = """
                     SELECT SUM(amount) AS total
                                          FROM transactions
                                          WHERE ClubID = ? AND TermID = ? and status = 'Approved' and type = 'income'""";
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
        return getTotalAmount != null ? getTotalAmount : BigDecimal.ZERO;
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
                                          FROM transactions
                                          WHERE ClubID = ? AND TermID = ? and status = 'Approved' and type = 'expense'""";
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
        return getTotalAmount != null ? getTotalAmount : BigDecimal.ZERO;
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
        return getTotalAmount != null ? getTotalAmount : BigDecimal.ZERO;
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
                     limit 8;""";
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
        MemberIncomeContributions invoice = FinancialDAO.getInvoiceByID(contributionID);
        String sql2 = """
                     INSERT INTO Transactions (ClubID, TermID, Type, Amount, TransactionDate, Description, Status, ReferenceID, CreatedBy)
                     VALUES
                     (?, ?, ?, ?, ?, ?, ?, ?, ?)""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            PreparedStatement ps2 = DBContext.getConnection().prepareStatement(sql2);

            ps.setInt(1, contributionID);
            ps2.setObject(1, invoice.getClubID());
            ps2.setObject(2, invoice.getTermID());
            ps2.setObject(3, "Income");
            ps2.setObject(4, invoice.getAmount());
            ps2.setObject(5, invoice.getPaidDate());
            ps2.setObject(6, invoice.getDescription());
            ps2.setObject(7, "Approved");
            ps2.setObject(8, invoice.getIncomeID());
            ps2.setObject(9, invoice.getUserID());

            int rowsAffected = ps.executeUpdate();
            int row = ps2.executeUpdate();
            return rowsAffected > 0 && row > 0;
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

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);

            ps.setInt(1, incomeID);

            int rowsAffected = ps.executeUpdate();

            return (rowsAffected > 0);
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

    public static List<MemberIncomeContributions> getInvoicesByUserID(String userID, String status, String termID, int page, int pageSize) {
        List<MemberIncomeContributions> invoices = new ArrayList<>();
        String sql = """
                     SELECT mic.*, u.FullName, u.Email, u.AvatarSrc, c.ClubName , i.Source, i.Description
                                     FROM MemberIncomeContributions mic 
                                     JOIN Users u ON mic.UserID = u.UserID 
                                     JOIN Clubs c ON mic.ClubID = c.ClubID 
                                     Join income i on mic.IncomeID = i.IncomeID
                                     WHERE mic.UserID = ?""";
        if (status != null && !status.isEmpty()) {
            sql += " AND mic.ContributionStatus = ?";
        }
        if (termID != null && !termID.isEmpty()) {
            sql += " AND mic.TermID = ?";
        }
        sql += " ORDER BY mic.CreatedAt DESC LIMIT ? OFFSET ?";

        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            int paramIndex = 1;
            ps.setString(paramIndex++, userID);
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (termID != null && !termID.isEmpty()) {
                ps.setString(paramIndex++, termID);
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex++, (page - 1) * pageSize);

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
                mic.setDueDate(rs.getTimestamp("DueDate"));

                mic.setUserName(rs.getString("FullName"));
                mic.setEmail(rs.getString("Email"));
                mic.setAvtSrc(rs.getString("AvatarSrc"));
                mic.setClubName(rs.getString("ClubName"));
                mic.setSource(rs.getString("Source"));
                mic.setDescription(rs.getString("Description"));

                invoices.add(mic);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public static int getTotalInvoices(String userID, String status, String termID) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM MemberIncomeContributions mic "
                + "WHERE mic.UserID = ?";
        if (status != null && !status.isEmpty()) {
            sql += " AND mic.ContributionStatus = ?";
        }
        if (termID != null && !termID.isEmpty()) {
            sql += " AND mic.TermID = ?";
        }

        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            int paramIndex = 1;
            ps.setString(paramIndex++, userID);
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (termID != null && !termID.isEmpty()) {
                ps.setString(paramIndex++, termID);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public static List<String> getTermIDs() {
        List<String> termIDs = new ArrayList<>();
        String sql = "SELECT DISTINCT TermID FROM Semesters ORDER BY TermID";
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                termIDs.add(rs.getString("TermID"));
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return termIDs;
    }

    public static MemberIncomeContributions getInvoiceByID(int contributionID) {
        MemberIncomeContributions mic = new MemberIncomeContributions();
        String sql = """
                     SELECT mic.*, u.FullName, u.Email, u.AvatarSrc, c.ClubName , i.Source, i.Description
                                                          FROM MemberIncomeContributions mic 
                                                          JOIN Users u ON mic.UserID = u.UserID 
                                                          JOIN Clubs c ON mic.ClubID = c.ClubID 
                                                          Join income i on mic.IncomeID = i.IncomeID
                                                          WHERE mic.contributionID = ?""";

        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, contributionID);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                mic.setContributionID(rs.getInt("ContributionID"));
                mic.setIncomeID(rs.getInt("IncomeID"));
                mic.setUserID(rs.getString("UserID"));
                mic.setClubID(rs.getInt("ClubID"));
                mic.setTermID(rs.getString("TermID"));
                mic.setAmount(rs.getBigDecimal("Amount"));
                mic.setContributionStatus(rs.getString("ContributionStatus"));
                mic.setPaidDate(rs.getTimestamp("PaidDate"));
                mic.setCreatedAt(rs.getTimestamp("CreatedAt"));
                mic.setDueDate(rs.getTimestamp("DueDate"));

                mic.setUserName(rs.getString("FullName"));
                mic.setEmail(rs.getString("Email"));
                mic.setAvtSrc(rs.getString("AvatarSrc"));
                mic.setClubName(rs.getString("ClubName"));
                mic.setSource(rs.getString("Source"));
                mic.setDescription(rs.getString("Description"));

            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mic;

    }

    public static boolean addTransaction(MemberIncomeContributions invoice, String type, String userID) {
        String sql = """
                     INSERT INTO Transactions (ClubID, TermID, Type, Amount, Description, ReferenceID, CreatedBy)
                     VALUES
                     (?, ?, ?, ?, ?, ?, ?)""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, invoice.getClubID());
            ps.setObject(2, invoice.getTermID());
            ps.setObject(3, type);
            ps.setObject(4, invoice.getAmount());
            ps.setObject(5, invoice.getDescription());
            ps.setObject(6, invoice.getIncomeID());
            ps.setObject(7, userID);
            int check = ps.executeUpdate();
            return check > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Transaction getTransactionByID(String type, String userID, int referenceID) {
        String sql = """
                     SELECT * FROM clubmanagementsystem.transactions
                     where type = ? and createdBy = ? and referenceID = ?
                     order by TransactionID desc
                     limit 1""";
        Transaction trans = new Transaction();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, type);
            ps.setObject(2, userID);
            ps.setObject(3, referenceID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                trans.setTransactionID(rs.getInt("TransactionID"));
                trans.setClubID(rs.getInt("ClubID"));
                trans.setTermID(rs.getString("TermID"));
                trans.setType(rs.getString("Type"));
                trans.setAmount(rs.getBigDecimal("Amount"));
                trans.setTransactionDate(rs.getTimestamp("TransactionDate"));
                trans.setDescription(rs.getString("Description"));
                trans.setAttachment(rs.getString("Attachment"));
                trans.setCreateBy(rs.getString("CreatedBy"));
                trans.setStatus(rs.getString("Status"));
                trans.setReferenceID(rs.getInt("ReferenceID"));
                trans.setCreatedDate(rs.getTimestamp("CreatedAt"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trans;
    }

    public static List<Transaction> getTransactionsByUser(String userID, String transResult, String termID, String clubID, int page) {
        List<Transaction> transactions = new ArrayList<>();
        int offset = (page - 1) * 10;

        StringBuilder sql = new StringBuilder("""
            SELECT * FROM clubmanagementsystem.transactions
            WHERE createdBy = ?
        """);

        if (transResult != null && !transResult.isEmpty()) {
            sql.append(" AND Status = ?");
        }
        if (termID != null && !termID.isEmpty()) {
            sql.append(" AND TermID = ?");
        }
        if (clubID != null && !clubID.isEmpty()) {
            sql.append(" AND ClubID = ?");
        }

        sql.append(" ORDER BY TransactionID DESC");
        sql.append(" LIMIT ? OFFSET ?");

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql.toString());
            int paramIndex = 1;
            ps.setString(paramIndex++, userID);

            if (transResult != null && !transResult.isEmpty()) {
                ps.setString(paramIndex++, transResult);
            }
            if (termID != null && !termID.isEmpty()) {
                ps.setString(paramIndex++, termID);
            }
            if (clubID != null && !clubID.isEmpty()) {
                ps.setString(paramIndex++, clubID);
            }
            ps.setInt(paramIndex++, 10);
            ps.setInt(paramIndex, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction trans = new Transaction();
                trans.setTransactionID(rs.getInt("TransactionID"));
                trans.setClubID(rs.getInt("ClubID"));
                trans.setTermID(rs.getString("TermID"));
                trans.setType(rs.getString("Type"));
                trans.setAmount(rs.getBigDecimal("Amount"));
                trans.setTransactionDate(rs.getTimestamp("TransactionDate"));
                trans.setDescription(rs.getString("Description"));
                trans.setAttachment(rs.getString("Attachment"));
                trans.setCreateBy(rs.getString("CreatedBy"));
                trans.setStatus(rs.getString("Status"));
                trans.setReferenceID(rs.getInt("ReferenceID"));
                trans.setCreatedDate(rs.getTimestamp("CreatedAt"));
                transactions.add(trans);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static int getTotalTransactionCount(String userID, String transResult, String termID, String clubID) {
        int totalRecords = 0;
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) as total FROM clubmanagementsystem.transactions WHERE createdBy = ?");

        if (transResult != null && !transResult.isEmpty()) {
            countSql.append(" AND Status = ?");
        }
        if (termID != null && !termID.isEmpty()) {
            countSql.append(" AND TermID = ?");
        }
        if (clubID != null && !clubID.isEmpty()) {
            countSql.append(" AND ClubID = ?");
        }

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(countSql.toString());
            int paramIndex = 1;
            ps.setString(paramIndex++, userID);

            if (transResult != null && !transResult.isEmpty()) {
                ps.setString(paramIndex++, transResult);
            }
            if (termID != null && !termID.isEmpty()) {
                ps.setString(paramIndex++, termID);
            }
            if (clubID != null && !clubID.isEmpty()) {
                ps.setString(paramIndex++, clubID);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalRecords = rs.getInt("total");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRecords;
    }

    public static List<Term> getAllTerms() {
        List<Term> terms = new ArrayList<>();
        String sql = "SELECT TermID, TermName FROM clubmanagementsystem.semesters ORDER BY TermName";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Term term = new Term();
                term.setTermID(rs.getString("TermID"));
                term.setTermName(rs.getString("TermName"));
                terms.add(term);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return terms;
    }

    public static List<Clubs> getClubsByUser(String userID) {
        List<Clubs> clubs = new ArrayList<>();
        String sql = """
                     SELECT DISTINCT c.ClubID, c.ClubName 
                                 FROM clubmanagementsystem.clubs c
                                 join userclubs uc on c.ClubID = uc.ClubID
                                 WHERE uc.UserID = ?
                                 ORDER BY c.ClubName""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Clubs club = new Clubs();
                club.setClubID(rs.getInt("ClubID"));
                club.setClubName(rs.getString("ClubName"));
                clubs.add(club);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }

    public static boolean updateMemberInvoices(MemberIncomeContributions invoice, Transaction trans) {
        String sql1 = """
                      UPDATE `clubmanagementsystem`.`memberincomecontributions`
                      SET
                      
                      `ContributionStatus` = ?,
                      `PaidDate` = ?
                      
                      WHERE `ContributionID` = ?;""";
        String sql2 = """
                      UPDATE `clubmanagementsystem`.`transactions`
                      SET
                      `TransactionDate` = ?,
                      `Status` = ?
                      WHERE `TransactionID` = ?;""";
        try {
            PreparedStatement ps1 = DBContext.getConnection().prepareStatement(sql1);
            PreparedStatement ps2 = DBContext.getConnection().prepareStatement(sql2);

            ps1.setObject(1, invoice.getContributionStatus());
            ps1.setObject(2, invoice.getPaidDate());
            ps1.setObject(3, invoice.getContributionID());

            ps2.setObject(1, trans.getTransactionDate());
            ps2.setObject(2, trans.getStatus());
            ps2.setObject(3, trans.getTransactionID());

            int check1 = ps1.executeUpdate();
            int check2 = ps2.executeUpdate();
            return check1 > 0 && check2 > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static List<Income> getIncomesByClubAndTerm(int clubID, String termID, String status, String source, int page, int pageSize) {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT * FROM Income WHERE ClubID = ? AND TermID = ?";
        if (status != null && !status.isEmpty()) {
            sql += " AND Status = ?";
        }
        if (source != null && !source.isEmpty()) {
            sql += " AND Source = ?";
        }
        sql += " ORDER BY IncomeDate DESC LIMIT ? OFFSET ?";

        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            int paramIndex = 1;
            ps.setInt(paramIndex++, clubID);
            ps.setString(paramIndex++, termID);
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (source != null && !source.isEmpty()) {
                ps.setString(paramIndex++, source);
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex++, (page - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Income income = new Income();
                income.setIncomeID(rs.getInt("IncomeID"));
                income.setClubID(rs.getInt("ClubID"));
                income.setTermID(rs.getString("TermID"));
                income.setSource(rs.getString("Source"));
                income.setAmount(rs.getBigDecimal("Amount"));
                income.setIncomeDate(rs.getTimestamp("IncomeDate"));
                income.setDescription(rs.getString("Description"));
                income.setAttachment(rs.getString("Attachment"));
                income.setStatus(rs.getString("Status"));
                incomes.add(income);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomes;
    }

    public static int getTotalIncomes(int clubID, String termID, String status, String source) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM Income WHERE ClubID = ? AND TermID = ?";
        if (status != null && !status.isEmpty()) {
            sql += " AND Status = ?";
        }
        if (source != null && !source.isEmpty()) {
            sql += " AND Source = ?";
        }

        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            int paramIndex = 1;
            ps.setInt(paramIndex++, clubID);
            ps.setString(paramIndex++, termID);
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (source != null && !source.isEmpty()) {
                ps.setString(paramIndex++, source);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public static boolean updateIncomeStatus(int incomeID, String userID) {
        String sql1 = """
                      UPDATE `clubmanagementsystem`.`income`
                      SET
                      
                      `Status` = "Đã nhận"
                      WHERE `IncomeID` = ?;""";
        Income income = FinancialDAO.findByIncomeID(incomeID);
        String sql2 = """
                      INSERT INTO `clubmanagementsystem`.`transactions`
                      (
                      `ClubID`,
                      `TermID`,
                      `Type`,
                      `Amount`,
                      `TransactionDate`,
                      `Description`,
                      `CreatedBy`,
                      `Status`,
                      `ReferenceID`)
                      VALUES
                      (
                      ?,
                      ?,
                      ?,
                      ?,
                      CURRENT_TIMESTAMP,
                      ?,
                      ?,
                      ?,
                      ?);""";

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql1);
            PreparedStatement ps2 = DBContext.getConnection().prepareStatement(sql2);
            ps.setObject(1, incomeID);
            ps2.setObject(1, income.getClubID());
            ps2.setObject(2, income.getTermID());
            ps2.setObject(3, "income");
            ps2.setObject(4, income.getAmount());
            ps2.setObject(5, income.getDescription());
            ps2.setObject(6, userID);
            ps2.setObject(7, "Approved");
            ps2.setObject(8, income.getIncomeID());
            int row = ps.executeUpdate();
            int row2 = 0;
            if (row > 0) {
                row2 = ps2.executeUpdate();

            }
            return row2 > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean insertIncome(Income income, String formattedStartedTime, String userID) {

        String sql2 = """
                      INSERT INTO `clubmanagementsystem`.`income`
                                            (
                                            `ClubID`,
                                            `TermID`,
                                            `Source`,
                                            `Amount`,
                                            `IncomeDate`,
                                            `Description`, 
                                            status)
                                            VALUES
                                            (
                                            ?,
                                            ?,
                                            ?,
                                            ?,
                                            current_timestamp,
                                            ?,
                                            ?)""";

        String sql3 = """
                      INSERT INTO `clubmanagementsystem`.`transactions`
                      (
                      `ClubID`,
                      `TermID`,
                      `Type`,
                      `Amount`, 
                      `Description`,
                      `CreatedBy`,
                      `Status`,
                      `ReferenceID`,
                      TransactionDate
                      )
                      VALUES
                      (
                      ?,
                      ?,
                      ?,
                      ?,
                      ?,
                      ?,
                      ?,
                      ?,
                      current_timestamp);""";
        String sql4 = """
                      INSERT INTO `clubmanagementsystem`.`memberincomecontributions`
                      (
                      `IncomeID`,
                      `UserID`,
                      `ClubID`,
                      `TermID`,
                      `Amount`,
                      `DueDate`)
                      VALUES
                      (
                      ?,
                      ?,
                      ?,
                      ?,
                      ?,
                      ?);""";
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql2);
            ps.setObject(1, income.getClubID());
            ps.setObject(2, income.getTermID());
            ps.setObject(3, income.getSource());
            ps.setObject(4, income.getAmount());
            ps.setObject(5, income.getDescription());
            ps.setObject(6, income.getStatus());
            int row = ps.executeUpdate();
            if (income.getStatus().equals("Đã nhận") && row > 0) {
                int in = FinancialDAO.findByIncomeIDNew(income.getClubID());
                PreparedStatement ps2 = DBContext.getConnection().prepareStatement(sql3);
                ps2.setObject(1, income.getClubID());
                ps2.setObject(2, income.getTermID());
                ps2.setObject(3, "income");
                ps2.setObject(4, income.getAmount());
                ps2.setObject(5, income.getDescription());
                ps2.setObject(6, userID);
                ps2.setObject(7, "Approved");
                ps2.setObject(8, in);
                int row2 = ps2.executeUpdate();
                return row2 > 0;
            }
            if (income.getSource().equals("Phí thành viên") && row > 0) {
                int in = FinancialDAO.findByIncomeIDNew(income.getClubID());
                PreparedStatement ps2 = DBContext.getConnection().prepareStatement(sql4);
                List<UserClub> list = UserClubDAO.findByClubID(income.getClubID());

       
                int row2 = 0;
                DepartmentDashboardDAO dao = new DepartmentDashboardDAO();
                int membercount = dao.getClubMemberCount(income.getClubID());
                
                for (UserClub uc : list) {
                    ps2.setObject(1, in);
                    ps2.setObject(2, uc.getUserID());
                    ps2.setObject(3, income.getClubID());
                    ps2.setObject(4, income.getTermID());
                    ps2.setObject(5, income.getAmount().divide(BigDecimal.valueOf(membercount)));
                    ps2.setObject(6, formattedStartedTime);
                    row2 += ps2.executeUpdate();
                    
                }
                
                return row2 == membercount;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    public static void main(String[] args) {
       
              
        List<UserClub> list = UserClubDAO.findByClubID(4);
        int i = 0;
        for (UserClub userClub : list) {
            i++;
        }
        System.out.println(i);
        
    }

    private static int findByIncomeIDNew(int clubID) {
        String sql = """
                     SELECT *
                                                               FROM Income
                                                               
                                                               WHERE clubID = ?
                                                               Order by incomeID desc limit 1""";
        int id = 0;
        try {

            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("IncomeID");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return id;
    }
    
    // Get completed income transactions with search, termID, and pagination
    public List<Transaction> getCompletedTransactions(int clubID, String search, String termID, int page, int pageSize) {
        List<Transaction> transactions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT incomeID, clubID, termID, source, amount, incomeDate, description, attachment, status, createdBy, createdByName " +
            "FROM Income " +
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
        sql.append(" ORDER BY incomeDate DESC");
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
                transaction.setTransactionID(rs.getInt("incomeID"));
                transaction.setClubID(rs.getInt("clubID"));
                transaction.setTermID(rs.getString("termID"));
                transaction.setType("Income");
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setTransactionDate(rs.getTimestamp("incomeDate"));
                transaction.setDescription(rs.getString("description"));
                transaction.setAttachment(rs.getString("attachment"));
                transaction.setStatus(rs.getString("status"));
                transaction.setCategory(rs.getString("source")); // Map source to category
                transaction.setCreateBy(rs.getString("createdBy"));
                transaction.setCreatedName(rs.getString("createdByName"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving completed income transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Count completed income transactions for pagination
    public int countCompletedTransactions(int clubID, String search, String termID) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) " +
            "FROM Income " +
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
            throw new RuntimeException("Error counting completed income transactions: " + e.getMessage());
        }
        return 0;
    }

    // Get total income for a club (optionally filtered by termID)
    public double getTotalIncome(int clubID, String termID) {
        StringBuilder sql = new StringBuilder(
            "SELECT SUM(amount) " +
            "FROM Income " +
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
            throw new RuntimeException("Error calculating total income: " + e.getMessage());
        }
        return 0.0;
    }

    // Get total pending income for a club (optionally filtered by termID)
    public double getTotalPendingIncome(int clubID, String termID) {
        StringBuilder sql = new StringBuilder(
            "SELECT SUM(amount) " +
            "FROM Income " +
            "WHERE clubID = ? AND status = 'Pending'"
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
            throw new RuntimeException("Error calculating total pending income: " + e.getMessage());
        }
        return 0.0;
    }

    // Get all term IDs for a club
    public List<String> getAllTermIDs(int clubID) {
        List<String> termIDs = new ArrayList<>();
        String sql = "SELECT DISTINCT termID FROM Income WHERE clubID = ? AND termID IS NOT NULL " +
                     "UNION " +
                     "SELECT DISTINCT termID FROM Expenses WHERE clubID = ? AND termID IS NOT NULL " +
                     "ORDER BY termID";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubID);
            stmt.setInt(2, clubID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                termIDs.add(rs.getString("termID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving term IDs: " + e.getMessage());
        }
        return termIDs;
    }
}

package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import models.RecruitmentStage;

public class RecruitmentStageDAO {
    
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // Tạo mới một vòng tuyển quân
    public int createRecruitmentStage(RecruitmentStage stage) {
        int newStageId = 0;
        try {
            // Kiểm tra tính hợp lệ của dữ liệu trước khi thêm vào DB
            if (stage.getRecruitmentID() <= 0) {
                System.out.println("DEBUG - RecruitmentStageDAO: RecruitmentID không hợp lệ: " + stage.getRecruitmentID());
                return 0;
            }
            
            if (stage.getStageName() == null || stage.getStageName().isEmpty()) {
                System.out.println("DEBUG - RecruitmentStageDAO: StageName không hợp lệ (null hoặc empty)");
                return 0;
            }
            
            conn = DBContext.getConnection();
            String sql = "INSERT INTO RecruitmentStages "
                    + "(RecruitmentID, StageName, Status, StartDate, EndDate, LocationID, Description, CreatedAt) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, stage.getRecruitmentID());
            ps.setString(2, stage.getStageName());
            
            // Tính toán status dựa trên ngày bắt đầu và kết thúc
            String status = stage.getStatus();
            if (status == null || status.isEmpty()) {
                status = determineStageStatus(stage.getStartDate(), stage.getEndDate());
            }
            ps.setString(3, status);
            
            ps.setTimestamp(4, stage.getStartDate() != null ? new Timestamp(stage.getStartDate().getTime()) : null);
            ps.setTimestamp(5, stage.getEndDate() != null ? new Timestamp(stage.getEndDate().getTime()) : null);
            ps.setInt(6, stage.getLocationID());
            ps.setString(7, stage.getDescription() != null ? stage.getDescription() : "");
            ps.setTimestamp(8, new Timestamp(new Date().getTime())); // Current timestamp
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newStageId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("DEBUG - RecruitmentStageDAO: Lỗi Exception khác: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return newStageId;
    }
    
    // Cập nhật thông tin vòng tuyển quân
    public boolean updateRecruitmentStage(RecruitmentStage stage) {
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE RecruitmentStages SET "
                    + "StageName = ?, Status = ?, StartDate = ?, EndDate = ?, "
                    + "LocationID = ?, Description = ? "
                    + "WHERE StageID = ? AND RecruitmentID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, stage.getStageName());
            
            // Tính toán status dựa trên ngày bắt đầu và kết thúc khi cập nhật
            String status = stage.getStatus();
            if (status == null || status.isEmpty()) {
                status = determineStageStatus(stage.getStartDate(), stage.getEndDate());
            }
            ps.setString(2, status);
            
            ps.setTimestamp(3, stage.getStartDate() != null ? new Timestamp(stage.getStartDate().getTime()) : null);
            ps.setTimestamp(4, stage.getEndDate() != null ? new Timestamp(stage.getEndDate().getTime()) : null);
            ps.setInt(5, stage.getLocationID());
            ps.setString(6, stage.getDescription());
            ps.setInt(7, stage.getStageID());
            ps.setInt(8, stage.getRecruitmentID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Chỉ cập nhật trạng thái của vòng tuyển quân
    public boolean updateStageStatus(int stageID, String status) {
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE RecruitmentStages SET Status = ? WHERE StageID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, stageID);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Lấy thông tin vòng tuyển theo ID
    public RecruitmentStage getStageById(int stageID) {
        RecruitmentStage stage = null;
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rs.*, l.LocationName, rc.Title as RecruitmentTitle "
                    + "FROM RecruitmentStages rs "
                    + "LEFT JOIN Locations l ON rs.LocationID = l.LocationID "
                    + "JOIN RecruitmentCampaigns rc ON rs.RecruitmentID = rc.RecruitmentID "
                    + "WHERE rs.StageID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stageID);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                stage = new RecruitmentStage(
                        rs.getInt("StageID"),
                        rs.getInt("RecruitmentID"),
                        rs.getString("StageName"),
                        rs.getString("Description"),
                        rs.getTimestamp("StartDate"),
                        rs.getTimestamp("EndDate"),
                        rs.getString("Status"),
                        rs.getInt("LocationID"),
                        null, // createdBy not stored in this table
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("LocationName"),
                        null, // locationAddress not stored directly
                        rs.getString("RecruitmentTitle"),
                        null // createdByName not available
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return stage;
    }
    
    // Lấy tất cả các vòng tuyển của một chiến dịch tuyển quân
    public List<RecruitmentStage> getStagesByRecruitmentId(int recruitmentID) {
        List<RecruitmentStage> stages = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rs.*, l.LocationName, rc.Title as RecruitmentTitle "
                    + "FROM RecruitmentStages rs "
                    + "LEFT JOIN Locations l ON rs.LocationID = l.LocationID "
                    + "JOIN RecruitmentCampaigns rc ON rs.RecruitmentID = rc.RecruitmentID "
                    + "WHERE rs.RecruitmentID = ? "
                    + "ORDER BY rs.StartDate";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, recruitmentID);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                RecruitmentStage stage = new RecruitmentStage(
                        rs.getInt("StageID"),
                        rs.getInt("RecruitmentID"),
                        rs.getString("StageName"),
                        rs.getString("Description"),
                        rs.getTimestamp("StartDate"),
                        rs.getTimestamp("EndDate"),
                        rs.getString("Status"),
                        rs.getInt("LocationID"),
                        null, // createdBy not stored in this table
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("LocationName"),
                        null, // locationAddress not stored directly
                        rs.getString("RecruitmentTitle"),
                        null // createdByName not available
                );
                stages.add(stage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return stages;
    }
    
    // Kiểm tra xem có sự trùng lặp thời gian giữa các vòng trong cùng một chiến dịch
    public boolean hasStageTimeOverlap(int recruitmentID, Date startDate, Date endDate, Integer excludeStageId) {
        try {
            conn = DBContext.getConnection();
            
            // SQL to check for time overlaps excluding the current stage if updating
            String sql = "SELECT COUNT(*) as count FROM RecruitmentStages "
                    + "WHERE RecruitmentID = ? AND "
                    + "((? BETWEEN StartDate AND EndDate) OR (? BETWEEN StartDate AND EndDate) OR "
                    + "(StartDate BETWEEN ? AND ?) OR (EndDate BETWEEN ? AND ?))";
            
            // If we're updating an existing stage, exclude it from the check
            if (excludeStageId != null) {
                sql += " AND StageID != ?";
            }
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, recruitmentID);
            ps.setTimestamp(2, new Timestamp(startDate.getTime()));
            ps.setTimestamp(3, new Timestamp(endDate.getTime()));
            ps.setTimestamp(4, new Timestamp(startDate.getTime()));
            ps.setTimestamp(5, new Timestamp(endDate.getTime()));
            ps.setTimestamp(6, new Timestamp(startDate.getTime()));
            ps.setTimestamp(7, new Timestamp(endDate.getTime()));
            
            if (excludeStageId != null) {
                ps.setInt(8, excludeStageId);
            }
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return false;
    }
    
    // Xóa một vòng tuyển quân (thường không khuyến khích)
    public boolean deleteRecruitmentStage(int stageID) {
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM RecruitmentStages WHERE StageID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stageID);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Phương thức hỗ trợ xác định trạng thái vòng dựa trên ngày
    public String determineStageStatus(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return "UPCOMING"; // Default if dates not provided
        }
        
        Date today = new Date(); // Current date
        
        if (today.before(startDate)) {
            return "UPCOMING";
        } else if (today.after(endDate)) {
            return "CLOSED";
        } else {
            return "ONGOING";
        }
    }
    
    /**
     * Kiểm tra xem vòng APPLICATION của một hoạt động tuyển quân có phản hồi chưa
     * @param recruitmentID ID của hoạt động tuyển quân
     * @return Số lượng đơn đăng ký cho vòng APPLICATION
     */
    public int countApplicationResponses(int recruitmentID) {
        int count = 0;
        try {
            conn = DBContext.getConnection();
            String sql = """
                SELECT COUNT(*) AS ApplicationCount
                FROM ApplicationStages ast
                JOIN RecruitmentStages rs ON ast.StageID = rs.StageID
                WHERE rs.StageName = 'APPLICATION'
                  AND rs.RecruitmentID = ?
                """;
                
            ps = conn.prepareStatement(sql);
            ps.setInt(1, recruitmentID);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("ApplicationCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return count;
    }
    
    /**
     * Kiểm tra xem hoạt động tuyển quân có đơn đăng ký cho vòng APPLICATION hay không
     * @param recruitmentID ID của hoạt động tuyển quân
     * @return true nếu có ít nhất 1 đơn đăng ký, false nếu không có
     */
    public boolean hasApplicationResponses(int recruitmentID) {
        return countApplicationResponses(recruitmentID) > 0;
    }

    // Phương thức hỗ trợ đóng kết nối cơ sở dữ liệu
    private void closeResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import models.DepartmentMeeting;
import models.DepartmentMeetingParticipant;

/**
 * Data Access Object for department meetings
 */
public class DepartmentMeetingDAO {

    /**
     * Lấy danh sách cuộc họp của một ban
     * @param departmentID ID của ban
     * @return Danh sách cuộc họp
     */
    public List<DepartmentMeeting> getMeetingsByDepartment(int departmentID) {
        List<DepartmentMeeting> meetings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT dm.*, d.DepartmentName " +
                         "FROM DepartmentMeeting dm " +
                         "JOIN Departments d ON dm.DepartmentID = d.DepartmentID " +
                         "WHERE dm.DepartmentID = ? " +
                         "ORDER BY dm.StartedTime DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                DepartmentMeeting meeting = mapMeetingFromResultSet(rs);
                
                // Đếm số người tham gia
                int participantCount = getParticipantCountByMeetingId(meeting.getDepartmentMeetingID());
                meeting.setParticipantCount(participantCount);
                
                meetings.add(meeting);
            }
        } catch (SQLException e) {
            System.out.println("Error getting department meetings: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return meetings;
    }
    
    /**
     * Đếm số người tham gia cuộc họp
     * @param meetingID ID của cuộc họp
     * @return Số người tham gia
     */
    public int getParticipantCountByMeetingId(int meetingID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT COUNT(*) as total FROM DepartmentMeetingParticipant WHERE MeetingID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, meetingID);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting participants: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return count;
    }
    
    /**
     * Lấy thông tin chi tiết cuộc họp theo ID
     * @param meetingID ID của cuộc họp
     * @return Thông tin chi tiết cuộc họp hoặc null nếu không tìm thấy
     */
    public DepartmentMeeting getMeetingById(int meetingID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DepartmentMeeting meeting = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT dm.*, d.DepartmentName " +
                         "FROM DepartmentMeeting dm " +
                         "JOIN Departments d ON dm.DepartmentID = d.DepartmentID " +
                         "WHERE dm.DepartmentMeetingID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, meetingID);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                meeting = mapMeetingFromResultSet(rs);
                
                // Lấy danh sách người tham gia
                meeting.setParticipants(getParticipantsByMeetingId(meetingID));
            }
        } catch (SQLException e) {
            System.out.println("Error getting meeting details: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return meeting;
    }
    
    /**
     * Lấy danh sách người tham gia cuộc họp
     * @param meetingID ID của cuộc họp
     * @return Danh sách người tham gia
     */
    public List<DepartmentMeetingParticipant> getParticipantsByMeetingId(int meetingID) {
        List<DepartmentMeetingParticipant> participants = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT dmp.*, u.FullName as UserName, u.Email as UserEmail, u.Avatar as UserAvatar, " +
                         "dm.DepartmentMeetingID, d.DepartmentName " +
                         "FROM DepartmentMeetingParticipant dmp " +
                         "JOIN Users u ON dmp.UserID = u.UserID " +
                         "JOIN DepartmentMeeting dm ON dmp.MeetingID = dm.DepartmentMeetingID " +
                         "JOIN Departments d ON dm.DepartmentID = d.DepartmentID " +
                         "WHERE dmp.MeetingID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, meetingID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                DepartmentMeetingParticipant participant = new DepartmentMeetingParticipant();
                participant.setParticipantID(rs.getInt("ParticipantID"));
                participant.setMeetingID(rs.getInt("MeetingID"));
                participant.setUserID(rs.getInt("UserID"));
                participant.setStatus(rs.getString("Status"));
                participant.setJoinedAt(rs.getTimestamp("JoinedAt"));
                
                // Set additional view fields
                participant.setUserName(rs.getString("UserName"));
                participant.setUserEmail(rs.getString("UserEmail"));
                participant.setUserAvatar(rs.getString("UserAvatar"));
                participant.setDepartmentName(rs.getString("DepartmentName"));
                
                participants.add(participant);
            }
        } catch (SQLException e) {
            System.out.println("Error getting meeting participants: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return participants;
    }
    
    /**
     * Tạo mới cuộc họp
     * @param meeting Thông tin cuộc họp cần tạo
     * @return ID của cuộc họp mới tạo, hoặc -1 nếu có lỗi
     */
    public int createMeeting(DepartmentMeeting meeting) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int meetingID = -1;
        
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO DepartmentMeeting (DepartmentID, URLMeeting, StartedTime) " +
                         "VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, meeting.getDepartmentID());
            stmt.setString(2, meeting.getUrlMeeting());
            stmt.setTimestamp(3, meeting.getStartedTime());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    meetingID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating department meeting: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return meetingID;
    }
    
    /**
     * Cập nhật thông tin cuộc họp
     * @param meeting Thông tin cuộc họp cần cập nhật
     * @return true nếu cập nhật thành công, false nếu có lỗi
     */
    public boolean updateMeeting(DepartmentMeeting meeting) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE DepartmentMeeting SET " +
                         "DepartmentID = ?, URLMeeting = ?, StartedTime = ? " +
                         "WHERE DepartmentMeetingID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, meeting.getDepartmentID());
            stmt.setString(2, meeting.getUrlMeeting());
            stmt.setTimestamp(3, meeting.getStartedTime());
            stmt.setInt(4, meeting.getDepartmentMeetingID());
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating department meeting: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    /**
     * Xóa cuộc họp
     * @param meetingID ID của cuộc họp cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    public boolean deleteMeeting(int meetingID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            
            // Xóa tất cả người tham gia trước
            String deleteParticipantsSQL = "DELETE FROM DepartmentMeetingParticipant WHERE MeetingID = ?";
            stmt = conn.prepareStatement(deleteParticipantsSQL);
            stmt.setInt(1, meetingID);
            stmt.executeUpdate();
            
            // Sau đó xóa cuộc họp
            String deleteMeetingSQL = "DELETE FROM DepartmentMeeting WHERE DepartmentMeetingID = ?";
            stmt = conn.prepareStatement(deleteMeetingSQL);
            stmt.setInt(1, meetingID);
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting department meeting: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    /**
     * Thêm người tham gia vào cuộc họp
     * @param participant Thông tin người tham gia
     * @return ID của người tham gia mới, hoặc -1 nếu có lỗi
     */
    public int addParticipant(DepartmentMeetingParticipant participant) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int participantID = -1;
        
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO DepartmentMeetingParticipant (MeetingID, UserID, Status) " +
                         "VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, participant.getMeetingID());
            stmt.setInt(2, participant.getUserID());
            stmt.setString(3, participant.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    participantID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding meeting participant: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return participantID;
    }
    
    /**
     * Kiểm tra xem người dùng đã tham gia cuộc họp chưa
     * @param meetingID ID của cuộc họp
     * @param userID ID của người dùng
     * @return true nếu đã tham gia, false nếu chưa
     */
    public boolean isUserParticipant(int meetingID, int userID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isParticipant = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT COUNT(*) as count FROM DepartmentMeetingParticipant " +
                         "WHERE MeetingID = ? AND UserID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, meetingID);
            stmt.setInt(2, userID);
            rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt("count") > 0) {
                isParticipant = true;
            }
        } catch (SQLException e) {
            System.out.println("Error checking participant: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return isParticipant;
    }
    
    /**
     * Cập nhật trạng thái người tham gia cuộc họp
     * @param participantID ID của người tham gia
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công, false nếu có lỗi
     */
    public boolean updateParticipantStatus(int participantID, String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE DepartmentMeetingParticipant SET Status = ? WHERE ParticipantID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, participantID);
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating participant status: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    /**
     * Xóa người tham gia khỏi cuộc họp
     * @param participantID ID của người tham gia cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    public boolean removeParticipant(int participantID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM DepartmentMeetingParticipant WHERE ParticipantID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, participantID);
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error removing participant: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    /**
     * Xóa người tham gia theo meeting và userID
     * @param meetingID ID của cuộc họp
     * @param userID ID của người dùng
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    public boolean removeParticipantByMeetingAndUser(int meetingID, int userID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM DepartmentMeetingParticipant WHERE MeetingID = ? AND UserID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, meetingID);
            stmt.setInt(2, userID);
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error removing participant: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    /**
     * Đếm tổng số cuộc họp của một ban
     * @param departmentID ID của ban
     * @return Số lượng cuộc họp
     */
    public int countMeetings(int departmentID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT COUNT(*) as total FROM DepartmentMeeting WHERE DepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentID);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting meetings: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return count;
    }
    
    /**
     * Đếm số cuộc họp sắp diễn ra của một ban
     * @param departmentID ID của ban
     * @return Số lượng cuộc họp sắp diễn ra
     */
    public int countUpcomingMeetings(int departmentID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT COUNT(*) as total FROM DepartmentMeeting " +
                         "WHERE DepartmentID = ? AND StartedTime > NOW()";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentID);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting upcoming meetings: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return count;
    }
    
    /**
     * Lấy danh sách các cuộc họp gần đây của một ban
     * @param departmentID ID của ban
     * @param limit Số lượng cuộc họp tối đa cần lấy
     * @return Danh sách các cuộc họp gần đây
     */
    public List<DepartmentMeeting> getRecentMeetings(int departmentID, int limit) {
        List<DepartmentMeeting> meetings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT dm.*, d.DepartmentName " +
                         "FROM DepartmentMeeting dm " +
                         "JOIN Departments d ON dm.DepartmentID = d.DepartmentID " +
                         "WHERE dm.DepartmentID = ? " +
                         "ORDER BY dm.StartedTime DESC " +
                         "LIMIT ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentID);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                DepartmentMeeting meeting = mapMeetingFromResultSet(rs);
                
                // Đếm số người tham gia
                int participantCount = getParticipantCountByMeetingId(meeting.getDepartmentMeetingID());
                meeting.setParticipantCount(participantCount);
                
                meetings.add(meeting);
            }
        } catch (SQLException e) {
            System.out.println("Error getting recent meetings: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return meetings;
    }
    
    /**
     * Maps a ResultSet row to a DepartmentMeeting object
     */
    private DepartmentMeeting mapMeetingFromResultSet(ResultSet rs) throws SQLException {
        DepartmentMeeting meeting = new DepartmentMeeting();
        meeting.setDepartmentMeetingID(rs.getInt("DepartmentMeetingID"));
        meeting.setDepartmentID(rs.getInt("DepartmentID"));
        meeting.setUrlMeeting(rs.getString("URLMeeting"));
        meeting.setStartedTime(rs.getTimestamp("StartedTime"));
        
        // Additional view fields
        if (rs.getMetaData().getColumnCount() > 4) {
            try {
                meeting.setDepartmentName(rs.getString("DepartmentName"));
            } catch (SQLException e) {
                // Column might not exist, ignore
            }
        }
        
        return meeting;
    }
    
    /**
     * Closes database resources
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.out.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
        
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing Connection: " + e.getMessage());
            }
        }
    }
}

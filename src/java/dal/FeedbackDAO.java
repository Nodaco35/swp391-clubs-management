package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Feedback;

public class FeedbackDAO extends DBContext {
    private static final Logger LOGGER = Logger.getLogger(FeedbackDAO.class.getName());
    
    public boolean insertFeedback(Feedback feedback) {
        String sql = "INSERT INTO Feedbacks (EventID, UserID, IsAnonymous, Rating, Content, " +
                    "Q1_Organization, Q2_Communication, Q3_Support, Q4_Relevance, " +
                    "Q5_Welcoming, Q6_Value, Q7_Timing, Q8_Participation, Q9_WillingnessToReturn) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, feedback.getEventID());
            ps.setString(2, feedback.getUserID());
            ps.setBoolean(3, feedback.isAnonymous());
            ps.setInt(4, feedback.getRating());
            ps.setString(5, feedback.getContent());
            ps.setInt(6, feedback.getQ1Organization());
            ps.setInt(7, feedback.getQ2Communication());
            ps.setInt(8, feedback.getQ3Support());
            ps.setInt(9, feedback.getQ4Relevance());
            ps.setInt(10, feedback.getQ5Welcoming());
            ps.setInt(11, feedback.getQ6Value());
            ps.setInt(12, feedback.getQ7Timing());
            ps.setInt(13, feedback.getQ8Participation());
            ps.setInt(14, feedback.getQ9WillingnessToReturn());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting feedback", e);
            return false;
        }
    }
    
    public boolean hasFeedbackForEvent(int eventID, String userID) {
        String sql = "SELECT COUNT(*) FROM Feedbacks WHERE EventID = ? AND UserID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, eventID);
            ps.setString(2, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if feedback exists", e);
        }
        return false;
    }
    

    public List<Feedback> getFeedbacksByEventID(int eventID) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM Feedbacks WHERE EventID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, eventID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = mapResultSetToFeedback(rs);
                    feedbacks.add(feedback);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting feedbacks for event", e);
        }
        return feedbacks;
    }
    

    public Feedback getFeedbackByID(int feedbackID) {
        String sql = "SELECT * FROM Feedbacks WHERE FeedbackID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, feedbackID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFeedback(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting feedback by ID", e);
        }
        return null;
    }

    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setFeedbackID(rs.getInt("FeedbackID"));
        feedback.setEventID(rs.getInt("EventID"));
        feedback.setUserID(rs.getString("UserID"));
        feedback.setAnonymous(rs.getBoolean("IsAnonymous"));
        feedback.setRating(rs.getInt("Rating"));
        feedback.setContent(rs.getString("Content"));
        feedback.setQ1Organization(rs.getInt("Q1_Organization"));
        feedback.setQ2Communication(rs.getInt("Q2_Communication"));
        feedback.setQ3Support(rs.getInt("Q3_Support"));
        feedback.setQ4Relevance(rs.getInt("Q4_Relevance"));
        feedback.setQ5Welcoming(rs.getInt("Q5_Welcoming"));
        feedback.setQ6Value(rs.getInt("Q6_Value"));
        feedback.setQ7Timing(rs.getInt("Q7_Timing"));
        feedback.setQ8Participation(rs.getInt("Q8_Participation"));
        feedback.setQ9WillingnessToReturn(rs.getInt("Q9_WillingnessToReturn"));
        feedback.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return feedback;
    }
    

    public Feedback getEventFeedbackStatistics(int eventID) {
        String sql = "SELECT AVG(Rating) as Rating, " +
                    "AVG(Q1_Organization) as Q1_Organization, " +
                    "AVG(Q2_Communication) as Q2_Communication, " +
                    "AVG(Q3_Support) as Q3_Support, " +
                    "AVG(Q4_Relevance) as Q4_Relevance, " +
                    "AVG(Q5_Welcoming) as Q5_Welcoming, " +
                    "AVG(Q6_Value) as Q6_Value, " +
                    "AVG(Q7_Timing) as Q7_Timing, " +
                    "AVG(Q8_Participation) as Q8_Participation, " +
                    "AVG(Q9_WillingnessToReturn) as Q9_WillingnessToReturn, " +
                    "COUNT(*) as FeedbackCount " +
                    "FROM Feedbacks WHERE EventID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, eventID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Feedback stats = new Feedback();
                    stats.setEventID(eventID);
                    stats.setRating((int)Math.round(rs.getDouble("Rating")));
                    stats.setQ1Organization((int)Math.round(rs.getDouble("Q1_Organization")));
                    stats.setQ2Communication((int)Math.round(rs.getDouble("Q2_Communication")));
                    stats.setQ3Support((int)Math.round(rs.getDouble("Q3_Support")));
                    stats.setQ4Relevance((int)Math.round(rs.getDouble("Q4_Relevance")));
                    stats.setQ5Welcoming((int)Math.round(rs.getDouble("Q5_Welcoming")));
                    stats.setQ6Value((int)Math.round(rs.getDouble("Q6_Value")));
                    stats.setQ7Timing((int)Math.round(rs.getDouble("Q7_Timing")));
                    stats.setQ8Participation((int)Math.round(rs.getDouble("Q8_Participation")));
                    stats.setQ9WillingnessToReturn((int)Math.round(rs.getDouble("Q9_WillingnessToReturn")));
                    return stats;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting feedback statistics", e);
        }
        return null;
    }
}

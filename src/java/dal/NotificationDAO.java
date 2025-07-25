/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.ArrayList;
import java.util.List;
import models.Notification;
import java.sql.*;

/**
 *
 * @author Admin
 */
public class NotificationDAO {

    public static List<Notification> findByUserId(String userID) {
        List<Notification> findByUserId = new ArrayList<>();

        String sql = """
                     SELECT * FROM Notifications
                     where ReceiverID = ?
                     order by CreatedDate desc;
                     ;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationID(rs.getInt("NotificationID"));
                notification.setTitle(rs.getString("Title"));
                notification.setContent(rs.getString("Content"));
                notification.setCreatedDate(rs.getTimestamp("CreatedDate"));
                notification.setReceiverID(rs.getString("ReceiverID"));
                notification.setPrioity(rs.getString("Priority"));
                notification.setStatus(rs.getString("Status"));
                notification.setSenderID(rs.getString("SenderID"));
                findByUserId.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserId != null ? findByUserId : null;

    }

    public static void delete(int id) {

        String sql = """
                     DELETE FROM `clubmanagementsystem`.`notifications`
                     WHERE NotificationID = ?;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void markAsRead(Integer id) {

        String sql = """
                     UPDATE Notifications
                     SET
                     Status = ?
                     WHERE NotificationID = ?;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, "READ");
            ps.setObject(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Notification findByNotificationID(Integer id) {

        String sql = """
                SELECT n.NotificationID, n.Title, n.Content, n.CreatedDate,
                            n.ReceiverID, n.Priority, n.Status, n.SenderID
                     FROM Notifications n
                     WHERE n.NotificationID = ?;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification noti = new Notification();
                noti.setNotificationID(rs.getInt("NotificationID"));
                noti.setTitle(rs.getString("Title"));
                noti.setContent(rs.getString("Content"));
                noti.setCreatedDate(rs.getTimestamp("CreatedDate"));
                noti.setReceiverID(rs.getString("ReceiverID"));
                noti.setPrioity(rs.getString("Priority"));
                noti.setStatus(rs.getString("Status"));
                noti.setSenderID(rs.getString("SenderID"));
                return noti;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Notification> findByUserIdAndStatus(String userID, String status) {
        List<Notification> findByUserId = new ArrayList<>();

        String sql = """
                     SELECT * FROM clubmanagementsystem.notifications
                     where ReceiverID = ? and status = ?
                     AND CreatedDate >= NOW() - INTERVAL 30 DAY
                     order by CreatedDate desc;
                     ;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ps.setObject(2, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationID(rs.getInt("NotificationID"));
                notification.setTitle(rs.getString("Title"));
                notification.setContent(rs.getString("Content"));
                notification.setCreatedDate(rs.getTimestamp("CreatedDate"));
                notification.setReceiverID(rs.getString("ReceiverID"));
                notification.setPrioity(rs.getString("Priority"));
                notification.setStatus(rs.getString("Status"));
                notification.setSenderID(rs.getString("SenderID"));
                findByUserId.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserId != null ? findByUserId : null;
    }

    public static boolean sentToPerson2(String senderID, String receiverID, String title, String content) {

        String sql = """
                     INSERT INTO `clubmanagementsystem`.`notifications`
                     (
                     `Title`,
                     `Content`,
                     `ReceiverID`,
                     `SenderID`)
                     VALUES
                     (
                     ?,
                     ?,
                     ?,
                     ?);""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, title);
            ps.setObject(2, content);
            ps.setObject(3, receiverID);
            ps.setObject(4, senderID);
            int row = ps.executeUpdate();
            return row>0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void sentToPerson(String senderID, String receiverID, String title, String content) {

        String sql = """
                     INSERT INTO `clubmanagementsystem`.`notifications`
                     (
                     `Title`,
                     `Content`,
                     `ReceiverID`,
                     `SenderID`)
                     VALUES
                     (
                     ?,
                     ?,
                     ?,
                     ?);""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, title);
            ps.setObject(2, content);
            ps.setObject(3, receiverID);
            ps.setObject(4, senderID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sentToPerson1(String senderID, String receiverID, String title, String content, String priority) {
        String sql = "INSERT INTO clubmanagementsystem.notifications (Title, Content, ReceiverID, SenderID, Priority) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, receiverID);
            ps.setString(4, senderID);
            ps.setString(5, priority);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Notification> findByUserIdAndImpotant(String userID, String prioity) {
        List<Notification> findByUserId = new ArrayList<>();

        String sql = """
                     SELECT * FROM clubmanagementsystem.notifications
                     where ReceiverID = ? and Priority = ?
                     AND CreatedDate >= NOW() - INTERVAL 30 DAY
                     order by CreatedDate desc;
                     ;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ps.setObject(2, prioity);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationID(rs.getInt("NotificationID"));
                notification.setTitle(rs.getString("Title"));
                notification.setContent(rs.getString("Content"));
                notification.setCreatedDate(rs.getTimestamp("CreatedDate"));
                notification.setReceiverID(rs.getString("ReceiverID"));
                notification.setPrioity(rs.getString("Priority"));
                notification.setStatus(rs.getString("Status"));
                notification.setSenderID(rs.getString("SenderID"));
                findByUserId.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserId != null ? findByUserId : null;
    }

    public static List<Notification> findByUserSenderID(String userID) {
        List<Notification> findByUserId = new ArrayList<>();

        String sql = """
                     SELECT * FROM clubmanagementsystem.notifications
                     where SenderID = ?
                     AND CreatedDate >= NOW() - INTERVAL 30 DAY
                     order by CreatedDate desc;
                      
                     ;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationID(rs.getInt("NotificationID"));
                notification.setTitle(rs.getString("Title"));
                notification.setContent(rs.getString("Content"));
                notification.setCreatedDate(rs.getTimestamp("CreatedDate"));
                notification.setReceiverID(rs.getString("ReceiverID"));
                notification.setPrioity(rs.getString("Priority"));
                notification.setStatus(rs.getString("Status"));
                notification.setSenderID(rs.getString("SenderID"));
                findByUserId.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserId != null ? findByUserId : null;
    }

    public static List<Notification> search(String userID, String keywords) {
        List<Notification> search = new ArrayList<>();
        String sql = """
                     SELECT `notifications`.`NotificationID`,
                            `notifications`.`Title`,
                            `notifications`.`Content`,
                            `notifications`.`CreatedDate`,
                            `notifications`.`ReceiverID`,
                            `notifications`.`SenderID`,
                            `notifications`.`Priority`,
                            `notifications`.`Status`
                     FROM `clubmanagementsystem`.`notifications`
                     WHERE `ReceiverID` = ?
                       AND (
                            `Title` LIKE CONCAT('%', ?, '%') OR
                            `Content` LIKE CONCAT('%', ?, '%')
                       )
                     ORDER BY `CreatedDate` DESC""";

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ps.setObject(2, keywords);
            ps.setObject(3, keywords);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationID(rs.getInt("NotificationID"));
                notification.setTitle(rs.getString("Title"));
                notification.setContent(rs.getString("Content"));
                notification.setCreatedDate(rs.getTimestamp("CreatedDate"));
                notification.setReceiverID(rs.getString("ReceiverID"));
                notification.setPrioity(rs.getString("Priority"));
                notification.setStatus(rs.getString("Status"));
                notification.setSenderID(rs.getString("SenderID"));
                search.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return search;
    }

    public static List<Notification> findRecentByUserID(String userID) {
        List<Notification> findRecentByUserID = new ArrayList<>();
        String sql = """
                     SELECT NotificationID, Title, Content, CreatedDate, ReceiverID, Priority, Status 
                     FROM Notifications WHERE CreatedDate >= DATE_SUB(NOW(), INTERVAL 7 DAY) AND ReceiverID = ?
                     ORDER BY CreatedDate DESC""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationID(rs.getInt("NotificationID"));
                notification.setTitle(rs.getString("Title"));
                notification.setContent(rs.getString("Content"));
                notification.setCreatedDate(rs.getTimestamp("CreatedDate"));
                notification.setReceiverID(rs.getString("ReceiverID"));
                notification.setPrioity(rs.getString("Priority"));
                notification.setStatus(rs.getString("Status"));

                findRecentByUserID.add(notification);
            }
        } catch (Exception e) {
        }
        return findRecentByUserID;
    }

}

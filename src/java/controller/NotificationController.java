/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.NotificationDAO;
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import models.Notification;
import models.Users;

/**
 *
 * @author Admin
 */
public class NotificationController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        Users user = (Users) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect("login");
        }
        if (action == null) {
            myNotification(request, response);
            return;
        } else if (action.equals("unreadNotifications")) {
            myUnreadNotification(request, response);
            return;
        } else if (action.equals("highNotifications")) {
            myHighNotifications(request, response);
            return;
        } else if (action.equals("sendNotifications")) {
            sendNotifications(request, response);
            return;
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "delete":
                delete(request, response);
                break;
            case "detail":
                markAsRead(request, response);
                break;
            case "sentNotification":
                sentToPerson(request, response);
                break;
            case "search":
                search(request, response);
                break;
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NotificationController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NotificationController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void myNotification(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("index.jsp");
        }
        List<Notification> notifications = new ArrayList<>();
        notifications = NotificationDAO.findByUserId(user.getUserID());
        for (Notification n : notifications) {
            if (n.getSenderID() != null) {
                Users sender = UserDAO.getUserById(n.getSenderID());

                n.setSenderAvatar(sender.getAvatar());
                n.setSenderName(sender.getFullName());
                n.setSenderEmail(sender.getEmail());
            } else {
                n.setSenderAvatar("img/avatar-he-thong.jpg");
                n.setSenderName("UniClub");
                n.setSenderEmail("funiclubs@gmail.com");
            }
        }
        request.setAttribute("notifications", notifications);
        request.getRequestDispatcher("view/notification.jsp").forward(request, response);

    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String error, msg;
        int id = Integer.parseInt(request.getParameter("id"));
        Notification noti = NotificationDAO.findByNotificationID(id);
        if (noti.getPrioity().equalsIgnoreCase("high")) {
            request.setAttribute("error", "Không thể xóa tin nhắn quan trọng!");
            myNotification(request, response);
            return;
        }
        request.setAttribute("msg", "Đã xóa thông báo!");
        NotificationDAO.delete(id);
        myNotification(request, response);
    }

    private void markAsRead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        NotificationDAO.markAsRead(id);
        String type = null;
        Users user = (Users) request.getSession().getAttribute("user");
        Notification noti = NotificationDAO.findByNotificationID(id);

        if (user == null) {
            response.sendRedirect("index.jsp");
        }
        if (!noti.getReceiverID().equals(user.getUserID())) {
            Users receiver = UserDAO.getUserById(noti.getReceiverID());

            noti.setReceiverAvatar(receiver.getAvatar());
            noti.setReceiverName(receiver.getFullName());
            noti.setReceiverEmail(receiver.getEmail());
            type = "send";
        }

        if (noti.getSenderID() != null) {
            Users senderUser = UserDAO.getUserById(noti.getSenderID());
            noti.setSenderName(senderUser.getFullName());
            noti.setSenderAvatar(senderUser.getAvatar());
            noti.setSenderEmail(senderUser.getEmail());
        } else {
            noti.setSenderName("UniClub");
            noti.setSenderAvatar("img/avatar-he-thong.jpg");
            noti.setSenderEmail("funiclubs@gmail.com");
        }
        request.setAttribute("type", type);
        request.getSession().setAttribute("user", user);
        request.setAttribute("noti", noti);
        request.getRequestDispatcher("view/detail-notification.jsp").forward(request, response);

    }

    private void myUnreadNotification(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        List<Notification> notifications = new ArrayList<>();
        String status = "UNREAD";
        notifications = NotificationDAO.findByUserIdAndStatus(user.getUserID(), status);
        for (Notification n : notifications) {
            if (n.getSenderID() != null) {
                Users sender = UserDAO.getUserById(n.getSenderID());

                n.setSenderAvatar(sender.getAvatar());
                n.setSenderName(sender.getFullName());
                n.setSenderEmail(sender.getEmail());
            } else {
                n.setSenderAvatar("img/avatar-he-thong.jpg");
                n.setSenderName("UniClub");
                n.setSenderEmail("funiclubs@gmail.com");
            }
        }
        request.setAttribute("notifications", notifications);
        request.getRequestDispatcher("view/notification.jsp").forward(request, response);
    }

    private void sentToPerson(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String senderID = request.getParameter("senderID");
        String receiverEmail = request.getParameter("receiverEmail");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        Users receiverUser = UserDAO.getUserByEmail(receiverEmail);
        if (receiverUser != null) {
            if (receiverUser.getUserID() == senderID) {
                String error = "Không thể tự gửi mail cho bản thân";
                
                myNotification(request, response);
            }
            boolean check = NotificationDAO.sentToPerson2(senderID, receiverUser.getUserID(), title, content);
            
            if (check) {
                request.setAttribute("success", "Gửi tin nhắn thành công");
                sendNotifications(request, response);
            }else{
                request.setAttribute("error", "Gửi tin nhắn thất bại");
                myNotification(request, response);
            }
            
        } else {
            String error = "Không tìm thấy người nhận";
            request.setAttribute("error", error);
            request.setAttribute("title", title);
            request.setAttribute("content", content);
            myNotification(request, response);
        }
    }

    private void myHighNotifications(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("index.jsp");
        }
        List<Notification> notifications = new ArrayList<>();
        String prioity = "HIGH";
        notifications = NotificationDAO.findByUserIdAndImpotant(user.getUserID(), prioity);
        for (Notification n : notifications) {
            if (n.getSenderID() != null) {
                Users sender = UserDAO.getUserById(n.getSenderID());

                n.setSenderAvatar(sender.getAvatar());
                n.setSenderName(sender.getFullName());
                n.setSenderEmail(sender.getEmail());
            } else {
                n.setSenderAvatar("img/avatar-he-thong.jpg");
                n.setSenderName("UniClub");
                n.setSenderEmail("funiclubs@gmail.com");
            }
        }
        request.setAttribute("notifications", notifications);
        request.getRequestDispatcher("view/notification.jsp").forward(request, response);
    }

    private void sendNotifications(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("index.jsp");
        }
        List<Notification> notifications = new ArrayList<>();

        notifications = NotificationDAO.findByUserSenderID(user.getUserID());
        for (Notification n : notifications) {

            Users receiver = UserDAO.getUserById(n.getReceiverID());

            n.setReceiverAvatar(receiver.getAvatar());
            n.setReceiverName(receiver.getFullName());
            n.setReceiverEmail(receiver.getEmail());

        }
        request.setAttribute("type", "send");
        request.setAttribute("notifications", notifications);
        request.getRequestDispatcher("view/notification.jsp").forward(request, response);
    }

    

    private void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String key_raw = request.getParameter("key");
        
        if (key_raw == null) {
            myNotification(request, response);
            return;
        }
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("index.jsp");
        }
        List<Notification> notifications = new ArrayList<>();
        notifications = NotificationDAO.search(user.getUserID(), key_raw.trim());
        for (Notification n : notifications) {
            if (n.getSenderID() != null) {
                Users sender = UserDAO.getUserById(n.getSenderID());
                n.setSenderAvatar(sender.getAvatar());
                n.setSenderName(sender.getFullName());
                n.setSenderEmail(sender.getEmail());
            } else {
                n.setSenderAvatar("img/avatar-he-thong.jpg");
                n.setSenderName("UniClub");
                n.setSenderEmail("funiclubs@gmail.com");
            }
        }
        request.setAttribute("key", key_raw);
        request.setAttribute("notifications", notifications);
        request.getRequestDispatcher("view/notification.jsp").forward(request, response);
        
    }
   
}

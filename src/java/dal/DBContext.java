package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {
    private static final Logger logger = Logger.getLogger(DBContext.class.getName());

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Driver MySQL 8+

            // Lấy từ biến môi trường (set trong docker-compose.yml)
            String url = System.getenv("DBMS_CONNECTION");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            // Kiểm tra null để tránh lỗi
            if (url == null || user == null || password == null) {
                logger.log(Level.SEVERE, "Missing environment variables: DBMS_CONNECTION, DB_USER, or DB_PASSWORD");
                return null;
            }

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database connection failed", e); // Thêm log exception
            return null;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }

    public static void main(String[] args) {
        // In rõ ràng để debug (xử lý null an toàn)
        String dbUrl = System.getenv("DBMS_CONNECTION");
        System.out.println("DBMS_CONNECTION from env: " + (dbUrl != null ? dbUrl : "NULL"));

        Connection conn = DBContext.getConnection();
        if (conn != null) {
            logger.info("Connection successful!");
        } else {
            logger.severe("Connection failed!");
        }
        DBContext.closeConnection(conn);
    }
}
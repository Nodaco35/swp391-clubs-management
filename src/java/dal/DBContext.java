
package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DBContext {
    private static final Logger logger = Logger.getLogger(DBContext.class.getName());
    
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // chú ý driver mới của MySQL
            String url = "jdbc:mysql://localhost:3306/ClubManagementSystem?zeroDateTimeBehavior=CONVERT_TO_NULL";
            String user = "root";
            String password = "root";            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database connection failed", e);
            return null;
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing connection", e);}
        }
    }
      public static void main(String[] args) {
        Connection conn = DBContext.getConnection();
        if (conn != null) {
            logger.info("Connection successful!");
        } else {
            logger.severe("Connection failed!");
        }
        DBContext.closeConnection(conn);
    }
}

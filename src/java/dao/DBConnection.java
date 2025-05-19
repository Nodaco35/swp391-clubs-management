
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // chú ý driver mới của MySQL
            String url = "jdbc:mysql://localhost:3306/managerclub?zeroDateTimeBehavior=CONVERT_TO_NULL";
            String user = "root";
            String password = "Thuylinh0203";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}




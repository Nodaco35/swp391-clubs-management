/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Admin
 */
public class DBContext_Duc {

    private static DBContext_Duc instance = new DBContext_Duc();
    Connection connection;

    public static DBContext_Duc getInstance() {
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private DBContext_Duc() {
        try {
            String user = "root";
<<<<<<< HEAD
            String password = "Ahy180104";
=======
            String password = "nodaco123";
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
            String url = "jdbc:mysql://localhost:3306/ClubManagementSystem?zeroDateTimeBehavior=CONVERT_TO_NULL";

            Class.forName("com.mysql.cj.jdbc.Driver"); // chú ý driver mới của MySQL
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console
            connection = null;
        }
    }

    public static void main(String[] args) {
        DBContext_Duc db = DBContext_Duc.getInstance();
        if (db.getConnection() != null) {
            System.out.println("Connection successful!");
        } else {
            System.out.println("Connection failed!");
        }
    }
}

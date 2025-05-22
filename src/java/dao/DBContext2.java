/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Admin
 */
public class DBContext2 {

    private static DBContext instance = new DBContext();
    Connection connection;

    public static DBContext getInstance() {
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private DBContext2() {
        try {
            String user = "root";
            String password = "Thuylinh0203";
            String url = "jdbc:mysql://localhost:3306/clubmanagementsystem?zeroDateTimeBehavior=CONVERT_TO_NULL";
            Class.forName("com.mysql.cj.jdbc.Driver"); // chú ý driver mới của MySQL
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console
            connection = null;
        }
    }

    public static void main(String[] args) {
        DBContext db = DBContext2.getInstance();
        if (db.getConnection() != null) {
            System.out.println("Connection successful!");
        } else {
            System.out.println("Connection failed!");
        }
    }
}


package dal;

import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // chú ý driver mới của MySQL
            String url = "jdbc:mysql://localhost:3306/ManagerClub?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "root";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Test coi ket noi toi database chua
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ManagerClub?useSSL=false&serverTimezone=UTC",
                    "root",
                    "root"
            );
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Users");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("FullName"));
                System.out.println(resultSet.getString("Email"));
                System.out.println(resultSet.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // DONE
    }
}




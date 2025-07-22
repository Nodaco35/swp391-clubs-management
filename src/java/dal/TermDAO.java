/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;
import java.util.*;
import models.*;
import java.sql.*;
/**
 *
 * @author he181
 */
public class TermDAO {

    public static Term getActiveSemester() {
        Term t = new Term();
        String sql = "SELECT * FROM Semesters\n"
                + "WHere Status = 'ACTIVE'";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                t.setTermID(rs.getString("TermID"));
                t.setTermName(rs.getString("TermName"));
                t.setStartDate(rs.getDate("StartDate"));
                t.setEndDate(rs.getDate("EndDate"));
                t.setStatus(rs.getString("Status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return t;
    }
    
}

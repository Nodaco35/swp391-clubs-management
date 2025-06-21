/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import models.Permission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Admin
 */
public class PermissionDAO {
    public static Permission findByPerId(int perId){
        
        String sql = "SELECT * FROM clubmanagementsystem.permissions;";
        try {
            DBContext_Duc db = DBContext_Duc.getInstance();
            PreparedStatement ps = db.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                Permission per = new Permission();
                per.setPermissionID(rs.getInt("PermissionID"));
                per.setPermissionName(rs.getString("PermissionName"));
                return per;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<Permission> findAll(){
        List<Permission> findAll = new ArrayList<>();
        String sql = """
                     SELECT `permissions`.`PermissionID`,
                         `permissions`.`PermissionName`,
                         `permissions`.`Description`
                     FROM `clubmanagementsystem`.`permissions`;""";
        DBContext_Duc db = DBContext_Duc.getInstance();
        try {
            PreparedStatement ps = db.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Permission per = new Permission();
                per.setPermissionID(rs.getInt("PermissionID"));
                per.setPermissionName(rs.getString("PermissionName"));
                findAll.add(per);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findAll;
    }
}

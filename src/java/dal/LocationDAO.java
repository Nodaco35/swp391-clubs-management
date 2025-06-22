package dal;

import models.Locations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {

    public boolean isLocationNameExists(String locationName, String typeLocation) {
        String sql = "SELECT COUNT(*) FROM Locations WHERE LocationName = ? AND TypeLocation = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, locationName.trim());
            ps.setString(2, typeLocation);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra trùng tên địa điểm", e);
        }
        return false;
    }

    public List<Locations> getLocationsByType(String typeLocation) {
        List<Locations> locations = new ArrayList<>();
        String sql = "SELECT LocationID, LocationName FROM Locations WHERE TypeLocation = ? ORDER BY LocationName";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, typeLocation);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Locations l = new Locations();
                l.setLocationID(rs.getInt("LocationID"));
                l.setLocationName(rs.getString("LocationName"));
                locations.add(l);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return locations;
    }

    public void addOffCampusLocation(String locationName) {
        String sql = "INSERT INTO Locations (LocationName, TypeLocation) VALUES (?, 'OffCampus')";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, locationName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int addLocation(String locationName) {
        String sql = "INSERT INTO Locations (LocationName, TypeLocation) VALUES (?, 'OffCampus')";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, locationName);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    // Thêm địa điểm mới (OffCampus)
//    public int addLocation(String locationName){
//        String sql = "INSERT INTO Locations (LocationName, TypeLocation) VALUES (?, 'OffCampus')";
//        try {
//
//            Connection connection = DBContext.getConnection();
//            PreparedStatement ps = connection.prepareStatement(sql);
//                ps.setString(1, locationName);
//                ps.executeUpdate();
//                ResultSet rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return 0;
//    }




}

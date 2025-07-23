package dal;

import models.Clubs;
import models.Department;
import models.Documents;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentsDAO {

    public List<Documents> findByClubID(int clubID) {
        List<Documents> documents = new ArrayList<>();
        String sql = "SELECT d.*, c.ClubName, dp.DepartmentName " +
                "FROM Documents d " +
                "JOIN Clubs c ON d.ClubID = c.ClubID " +
                "JOIN Departments dp ON d.DepartmentID = dp.DepartmentID " +
                "WHERE d.ClubID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Documents doc = new Documents();
                    doc.setDocumentID(rs.getInt("DocumentID"));
                    doc.setDocumentName(rs.getString("DocumentName"));
                    doc.setDescription(rs.getString("Description"));
                    doc.setDocumentURL(rs.getString("DocumentURL"));
                    doc.setDocumentType(rs.getString("DocumentType"));
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubName(rs.getString("ClubName"));
                    doc.setClub(club);
                    Department department = new Department();
                    department.setDepartmentID(rs.getInt("DepartmentID"));
                    department.setDepartmentName(rs.getString("DepartmentName"));
                    doc.setDepartment(department);
                    documents.add(doc);
                }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return documents;
    }

    public void insertDocument(Documents document) throws SQLException {
        String sql = "INSERT INTO Documents (DocumentName, Description, DocumentURL, DocumentType, ClubID, DepartmentID) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, document.getDocumentName());
            ps.setString(2, document.getDescription());
            ps.setString(3, document.getDocumentURL());
            ps.setString(4, document.getDocumentType());
            ps.setInt(5, document.getClub().getClubID());
            ps.setInt(6, document.getDepartment().getDepartmentID());
            ps.executeUpdate();
        }
    }

    public void updateDocument(Documents document) throws SQLException {
        String sql = "UPDATE Documents SET DocumentName = ?, Description = ?, DocumentURL = ?, DocumentType = ?, DepartmentID = ? WHERE DocumentID = ?";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, document.getDocumentName());
            ps.setString(2, document.getDescription());
            ps.setString(3, document.getDocumentURL());
            ps.setString(4, document.getDocumentType());
            ps.setInt(5, document.getDepartment().getDepartmentID());
            ps.setInt(6, document.getDocumentID());
            ps.executeUpdate();
        }
    }

    public void deleteDocument(int documentID) throws SQLException {
        String sql = "DELETE FROM Documents WHERE DocumentID = ?";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, documentID);
            ps.executeUpdate();
        }
    }

    public Documents findByDocumentID(int documentID) {
        String sql = "SELECT d.*, c.ClubName, dp.DepartmentName " +
                "FROM Documents d " +
                "JOIN Clubs c ON d.ClubID = c.ClubID " +
                "JOIN Departments dp ON d.DepartmentID = dp.DepartmentID " +
                "WHERE d.DocumentID = ?";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, documentID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Documents doc = new Documents();
                    doc.setDocumentID(rs.getInt("DocumentID"));
                    doc.setDocumentName(rs.getString("DocumentName"));
                    doc.setDescription(rs.getString("Description"));
                    doc.setDocumentURL(rs.getString("DocumentURL"));
                    doc.setDocumentType(rs.getString("DocumentType"));
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubName(rs.getString("ClubName"));
                    doc.setClub(club);
                    Department department = new Department();
                    department.setDepartmentID(rs.getInt("DepartmentID"));
                    department.setDepartmentName(rs.getString("DepartmentName"));
                    doc.setDepartment(department);
                    return doc;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public Integer addDocument(Documents doc) {
        String sql = "INSERT INTO Documents (DocumentName, DocumentURL, DocumentType, ClubID, DepartmentID) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, doc.getDocumentName());
            ps.setString(2, doc.getDocumentURL());
            ps.setString(3, doc.getDocumentType());
            ps.setInt(4, doc.getClub().getClubID());
            ps.setInt(5, doc.getDepartment().getDepartmentID());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Documents getDocumentByID(int documentID) {
        String sql = "SELECT * FROM Documents WHERE DocumentID = ?";
        DepartmentDAO deptDAO = new DepartmentDAO();
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, documentID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Documents doc = new Documents();
                doc.setDocumentID(rs.getInt("DocumentID"));
                doc.setDocumentName(rs.getString("DocumentName"));
                doc.setDocumentURL(rs.getString("DocumentURL"));
                doc.setDocumentType(rs.getString("DocumentType"));
                Clubs club = new Clubs();
                club.setClubID(rs.getInt("ClubID"));
                doc.setClub(club);
                Department dept = deptDAO.getDepartmentByID(rs.getInt("DepartmentID"));
                doc.setDepartment(dept);
                return doc;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Documents> getDocumentsByClubID(int clubID) {
        List<Documents> documents = new ArrayList<>();
        String sql = "SELECT d.*, c.ClubName, dp.DepartmentName " +
                "FROM Documents d " +
                "JOIN Clubs c ON d.ClubID = c.ClubID " +
                "JOIN Departments dp ON d.DepartmentID = dp.DepartmentID " +
                "WHERE d.ClubID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Documents doc = new Documents();
                doc.setDocumentID(rs.getInt("DocumentID"));
                doc.setDocumentName(rs.getString("DocumentName"));
                doc.setDescription(rs.getString("Description"));
                doc.setDocumentURL(rs.getString("DocumentURL"));
                doc.setDocumentType(rs.getString("DocumentType"));
                Clubs club = new Clubs();
                club.setClubID(rs.getInt("ClubID"));
                club.setClubName(rs.getString("ClubName"));
                doc.setClub(club);
                Department department = new Department();
                department.setDepartmentID(rs.getInt("DepartmentID"));
                department.setDepartmentName(rs.getString("DepartmentName"));
                doc.setDepartment(department);
                documents.add(doc);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return documents;
    }
}

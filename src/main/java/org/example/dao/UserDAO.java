package org.example.dao;

import org.example.model.User;
import org.example.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private final Connection connection;

    public UserDAO() {
        this.connection = Database.getConnection();
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public java.util.List<User> getAll() {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT u.*, r.name as role_name FROM user u JOIN role r ON u.role_id = r.id ORDER BY u.id DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User u = mapResultSetToUser(rs);
                u.setRoleName(rs.getString("role_name"));
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(User u) {
        String sql = "INSERT INTO user (role_id, username, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, u.getRoleId());
            pstmt.setString(2, u.getUsername());
            pstmt.setString(3, u.getEmail());
            pstmt.setString(4, u.getPassword());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(User u) {
        String sql = "UPDATE user SET role_id = ?, username = ?, email = ?, password = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, u.getRoleId());
            pstmt.setString(2, u.getUsername());
            pstmt.setString(3, u.getEmail());
            pstmt.setString(4, u.getPassword());
            pstmt.setInt(5, u.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<User> search(String keyword) {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT u.*, r.name as role_name FROM user u JOIN role r ON u.role_id = r.id " +
                     "WHERE u.username LIKE ? OR u.email LIKE ? ORDER BY u.id DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String term = "%" + keyword + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    u.setRoleName(rs.getString("role_name"));
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setRoleId(rs.getInt("role_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}

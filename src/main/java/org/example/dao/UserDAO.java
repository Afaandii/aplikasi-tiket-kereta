package org.example.dao;

import org.example.model.User;
import org.example.utils.Database;
import org.example.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    boolean passwordMatches = false;
                    boolean needsUpgrade = false;

                    if (storedPassword != null && storedPassword.startsWith("$2a$")) {
                        // Standard BCrypt check
                        passwordMatches = PasswordUtil.check(password, storedPassword);
                    } else {
                        // Legacy plain-text check
                        passwordMatches = password.equals(storedPassword);
                        needsUpgrade = passwordMatches;
                    }

                    if (passwordMatches) {
                        User user = mapResultSetToUser(rs);
                        if (needsUpgrade) {
                            // Automatically upgrade to hashed password
                            String newHash = PasswordUtil.hash(password);
                            updatePassword(user.getId(), newHash);
                        }
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updatePassword(int userId, String newHash) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newHash);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            System.out.println("Password for user ID " + userId + " has been upgraded to BCrypt hash.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public java.util.List<User> getAll() {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT u.*, r.nama_role as role_name FROM user u JOIN role r ON u.role_id = r.id ORDER BY u.id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
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

    public java.util.List<User> getByRoleId(int roleId) {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT u.*, r.nama_role as role_name FROM user u JOIN role r ON u.role_id = r.id WHERE u.role_id = ? ORDER BY u.id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);
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

    public boolean insert(User u) {
        String sql = "INSERT INTO user (role_id, username, email, password, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        String sql = "UPDATE user SET role_id = ?, username = ?, email = ?, password = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<User> search(String keyword) {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT u.*, r.nama_role as role_name FROM user u JOIN role r ON u.role_id = r.id " +
                     "WHERE u.username LIKE ? OR u.email LIKE ? ORDER BY u.id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

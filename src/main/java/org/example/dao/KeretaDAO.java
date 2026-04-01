package org.example.dao;

import org.example.model.Kereta;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KeretaDAO {

    public List<Kereta> getAll() {
        List<Kereta> list = new ArrayList<>();
        String sql = "SELECT * FROM kereta ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Kereta k = new Kereta();
                k.setId(rs.getInt("id"));
                k.setKodeKereta(rs.getString("kode_kereta"));
                k.setNamaKereta(rs.getString("nama_kereta"));
                k.setTipeKereta(rs.getString("tipe_kereta"));
                k.setCreatedAt(rs.getTimestamp("created_at"));
                k.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Kereta k) {
        String sql = "INSERT INTO kereta (kode_kereta, nama_kereta, tipe_kereta, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, k.getKodeKereta());
            pstmt.setString(2, k.getNamaKereta());
            pstmt.setString(3, k.getTipeKereta());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Kereta k) {
        String sql = "UPDATE kereta SET kode_kereta = ?, nama_kereta = ?, tipe_kereta = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, k.getKodeKereta());
            pstmt.setString(2, k.getNamaKereta());
            pstmt.setString(3, k.getTipeKereta());
            pstmt.setInt(4, k.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM kereta WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Kereta> search(String keyword) {
        List<Kereta> list = new ArrayList<>();
        String sql = "SELECT * FROM kereta WHERE kode_kereta LIKE ? OR nama_kereta LIKE ? OR tipe_kereta LIKE ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Kereta k = new Kereta();
                    k.setId(rs.getInt("id"));
                    k.setKodeKereta(rs.getString("kode_kereta"));
                    k.setNamaKereta(rs.getString("nama_kereta"));
                    k.setTipeKereta(rs.getString("tipe_kereta"));
                    k.setCreatedAt(rs.getTimestamp("created_at"));
                    k.setUpdatedAt(rs.getTimestamp("updated_at"));
                    list.add(k);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

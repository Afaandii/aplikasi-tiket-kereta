package org.example.dao;

import org.example.model.KelasKereta;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KelasKeretaDAO {

    public List<KelasKereta> getAll() {
        List<KelasKereta> list = new ArrayList<>();
        String sql = "SELECT * FROM kelas_kereta ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                KelasKereta k = mapResultSetToKelasKereta(rs);
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(KelasKereta k) {
        String sql = "INSERT INTO kelas_kereta (nama_kelas_kereta, created_at, updated_at) VALUES (?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, k.getNamaKelasKereta());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KelasKereta k) {
        String sql = "UPDATE kelas_kereta SET nama_kelas_kereta = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, k.getNamaKelasKereta());
            pstmt.setInt(2, k.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM kelas_kereta WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<KelasKereta> search(String keyword) {
        List<KelasKereta> list = new ArrayList<>();
        String sql = "SELECT * FROM kelas_kereta WHERE nama_kelas_kereta LIKE ? ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToKelasKereta(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private KelasKereta mapResultSetToKelasKereta(ResultSet rs) throws SQLException {
        KelasKereta k = new KelasKereta();
        k.setId(rs.getInt("id"));
        k.setNamaKelasKereta(rs.getString("nama_kelas_kereta"));
        k.setCreatedAt(rs.getTimestamp("created_at"));
        k.setUpdatedAt(rs.getTimestamp("updated_at"));
        return k;
    }
}

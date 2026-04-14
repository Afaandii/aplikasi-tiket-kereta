package org.example.dao;

import org.example.model.Gerbong;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GerbongDAO {

    public List<Gerbong> getAll() {
        List<Gerbong> list = new ArrayList<>();
        String sql = "SELECT g.*, k.nama_kereta, kk.nama_kelas_kereta as nama_kelas " +
                     "FROM gerbong g " +
                     "JOIN kereta k ON g.kereta_id = k.id " +
                     "JOIN kelas_kereta kk ON g.kelas_id = kk.id " +
                     "ORDER BY g.id ASC";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Gerbong g = new Gerbong();
                g.setId(rs.getInt("id"));
                g.setKeretaId(rs.getInt("kereta_id"));
                g.setKelasId(rs.getInt("kelas_id"));
                g.setNomorGerbong(rs.getString("nomor_gerbong"));
                g.setStokGerbong(rs.getInt("stok_gerbong"));
                g.setCreatedAt(rs.getTimestamp("created_at"));
                g.setUpdatedAt(rs.getTimestamp("updated_at"));
                g.setNamaKereta(rs.getString("nama_kereta"));
                g.setNamaKelas(rs.getString("nama_kelas"));
                list.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Gerbong> getByKeretaId(int keretaId) {
        List<Gerbong> list = new ArrayList<>();
        String sql = "SELECT g.*, k.nama_kereta, kk.nama_kelas_kereta as nama_kelas " +
                     "FROM gerbong g " +
                     "JOIN kereta k ON g.kereta_id = k.id " +
                     "JOIN kelas_kereta kk ON g.kelas_id = kk.id " +
                     "WHERE g.kereta_id = ? " +
                     "ORDER BY g.id ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, keretaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Gerbong g = new Gerbong();
                    g.setId(rs.getInt("id"));
                    g.setKeretaId(rs.getInt("kereta_id"));
                    g.setKelasId(rs.getInt("kelas_id"));
                    g.setNomorGerbong(rs.getString("nomor_gerbong"));
                    g.setStokGerbong(rs.getInt("stok_gerbong"));
                    g.setCreatedAt(rs.getTimestamp("created_at"));
                    g.setUpdatedAt(rs.getTimestamp("updated_at"));
                    g.setNamaKereta(rs.getString("nama_kereta"));
                    g.setNamaKelas(rs.getString("nama_kelas"));
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(Gerbong g) {
        String sql = "INSERT INTO gerbong (kereta_id, kelas_id, nomor_gerbong, stok_gerbong, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, g.getKeretaId());
            pstmt.setInt(2, g.getKelasId());
            pstmt.setString(3, g.getNomorGerbong());
            pstmt.setInt(4, g.getStokGerbong());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the new ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(Gerbong g) {
        String sql = "UPDATE gerbong SET kereta_id = ?, kelas_id = ?, nomor_gerbong = ?, stok_gerbong = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, g.getKeretaId());
            pstmt.setInt(2, g.getKelasId());
            pstmt.setString(3, g.getNomorGerbong());
            pstmt.setInt(4, g.getStokGerbong());
            pstmt.setInt(5, g.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM gerbong WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reduceStock(int keretaId, int amount) {
        String sql = "UPDATE gerbong SET stok_gerbong = stok_gerbong - ?, updated_at = NOW() WHERE kereta_id = ? AND stok_gerbong >= ? LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, amount);
            pstmt.setInt(2, keretaId);
            pstmt.setInt(3, amount);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

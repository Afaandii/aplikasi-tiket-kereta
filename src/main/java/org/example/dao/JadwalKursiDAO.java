package org.example.dao;

import org.example.model.JadwalKursi;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalKursiDAO {

    public List<JadwalKursi> getByJadwalId(int jadwalId) {
        List<JadwalKursi> list = new ArrayList<>();
        String sql = "SELECT jk.*, k.kode_kursi, k.baris_kursi " +
                     "FROM jadwal_kursi jk " +
                     "JOIN kursi k ON jk.kursi_id = k.id " +
                     "WHERE jk.jadwal_id = ? " +
                     "ORDER BY k.baris_kursi ASC, k.kode_kursi ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jadwalId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JadwalKursi jk = new JadwalKursi();
                    jk.setId(rs.getInt("id"));
                    jk.setJadwalId(rs.getInt("jadwal_id"));
                    jk.setKursiId(rs.getInt("kursi_id"));
                    jk.setStatus(rs.getString("status"));
                    jk.setCreatedAt(rs.getTimestamp("created_at"));
                    jk.setUpdatedAt(rs.getTimestamp("updated_at"));
                    jk.setKodeKursi(rs.getString("kode_kursi"));
                    jk.setBarisKursi(rs.getInt("baris_kursi"));
                    list.add(jk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE jadwal_kursi SET status = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByJadwalId(int jadwalId) {
        String sql = "DELETE FROM jadwal_kursi WHERE jadwal_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jadwalId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper to initialize seats when a new schedule is created
    public boolean initializeSeats(int jadwalId, int keretaId) {
        // First get all seats for this train's carriages
        String sql = "INSERT INTO jadwal_kursi (jadwal_id, kursi_id, status, created_at, updated_at) " +
                     "SELECT ?, k.id, 'Tersedia', NOW(), NOW() " +
                     "FROM kursi k " +
                     "JOIN gerbong g ON k.gerbong_id = g.id " +
                     "WHERE g.kereta_id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jadwalId);
            pstmt.setInt(2, keretaId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

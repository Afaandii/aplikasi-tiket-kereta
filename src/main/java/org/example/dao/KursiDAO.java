package org.example.dao;

import org.example.model.Kursi;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KursiDAO {

    public List<Kursi> getByGerbongId(int gerbongId) {
        List<Kursi> list = new ArrayList<>();
        String sql = "SELECT * FROM kursi WHERE gerbong_id = ? ORDER BY baris_kursi ASC, kode_kursi ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gerbongId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Kursi k = new Kursi();
                    k.setId(rs.getInt("id"));
                    k.setGerbongId(rs.getInt("gerbong_id"));
                    k.setBarisKursi(rs.getInt("baris_kursi"));
                    k.setKodeKursi(rs.getString("kode_kursi"));
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

    public boolean insertBatch(List<Kursi> list) {
        String sql = "INSERT INTO kursi (gerbong_id, baris_kursi, kode_kursi, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            for (Kursi k : list) {
                pstmt.setInt(1, k.getGerbongId());
                pstmt.setInt(2, k.getBarisKursi());
                pstmt.setString(3, k.getKodeKursi());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByGerbongId(int gerbongId) {
        String sql = "DELETE FROM kursi WHERE gerbong_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gerbongId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM kursi WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package org.example.dao;

import org.example.model.Gerbong;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GerbongDAO {

    public List<Gerbong> getAll() {
        List<Gerbong> list = new ArrayList<>();
        String sql = "SELECT g.*, k.nama_kereta, kk.nama_kelas " +
                     "FROM gerbong g " +
                     "JOIN kereta k ON g.kereta_id = k.id " +
                     "JOIN kelas_kereta kk ON g.kelas_kereta_id = kk.id " +
                     "ORDER BY g.id DESC";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Gerbong g = new Gerbong();
                g.setId(rs.getInt("id"));
                g.setKeretaId(rs.getInt("kereta_id"));
                g.setKelasKeretaId(rs.getInt("kelas_kereta_id"));
                g.setNamaGerbong(rs.getString("nama_gerbong"));
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

    public int insert(Gerbong g) {
        String sql = "INSERT INTO gerbong (kereta_id, kelas_kereta_id, nama_gerbong) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, g.getKeretaId());
            pstmt.setInt(2, g.getKelasKeretaId());
            pstmt.setString(3, g.getNamaGerbong());
            
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
        String sql = "UPDATE gerbong SET kereta_id = ?, kelas_kereta_id = ?, nama_gerbong = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, g.getKeretaId());
            pstmt.setInt(2, g.getKelasKeretaId());
            pstmt.setString(3, g.getNamaGerbong());
            pstmt.setInt(4, g.getId());
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
}

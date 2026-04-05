package org.example.dao;

import org.example.model.JadwalHarga;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalHargaDAO {

    public List<JadwalHarga> getByJadwalId(int jadwalId) {
        List<JadwalHarga> list = new ArrayList<>();
        String sql = "SELECT jh.*, kk.nama_kelas_kereta " +
                     "FROM jadwal_harga jh " +
                     "JOIN kelas_kereta kk ON jh.kelas_id = kk.id " +
                     "WHERE jh.jadwal_id = ? " +
                     "ORDER BY jh.id ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jadwalId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JadwalHarga jh = new JadwalHarga();
                    jh.setId(rs.getInt("id"));
                    jh.setJadwalId(rs.getInt("jadwal_id"));
                    jh.setKelasId(rs.getInt("kelas_id"));
                    jh.setHargaTiket(rs.getInt("harga_tiket"));
                    jh.setCreatedAt(rs.getTimestamp("created_at"));
                    jh.setUpdatedAt(rs.getTimestamp("updated_at"));
                    jh.setNamaKelas(rs.getString("nama_kelas_kereta"));
                    list.add(jh);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(JadwalHarga jh) {
        String sql = "INSERT INTO jadwal_harga (jadwal_id, kelas_id, harga_tiket, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jh.getJadwalId());
            pstmt.setInt(2, jh.getKelasId());
            pstmt.setInt(3, jh.getHargaTiket());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(JadwalHarga jh) {
        String sql = "UPDATE jadwal_harga SET harga_tiket = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jh.getHargaTiket());
            pstmt.setInt(2, jh.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM jadwal_harga WHERE id = ?";
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

package org.example.dao;

import org.example.model.Jadwal;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalDAO {

    public List<Jadwal> getAll() {
        List<Jadwal> list = new ArrayList<>();
        String sql = "SELECT j.*, k.nama_kereta, s1.nama_stasiun as awal, s2.nama_stasiun as tujuan " +
                     "FROM jadwal j " +
                     "JOIN kereta k ON j.kereta_id = k.id " +
                     "JOIN stasiun s1 ON j.stasiun_awal_id = s1.id " +
                     "JOIN stasiun s2 ON j.stasiun_tujuan_id = s2.id " +
                     "ORDER BY j.id DESC";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Jadwal j = mapResultSetToJadwal(rs);
                list.add(j);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Jadwal j) {
        String sql = "INSERT INTO jadwal (kereta_id, stasiun_awal_id, stasiun_tujuan_id, harga_tiket, waktu_berangkat, waktu_tiba, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, j.getKeretaId());
            pstmt.setInt(2, j.getStasiunAwalId());
            pstmt.setInt(3, j.getStasiunTujuanId());
            pstmt.setInt(4, j.getHargaTiket());
            pstmt.setTimestamp(5, j.getWaktuBerangkat());
            pstmt.setTimestamp(6, j.getWaktuTiba());
            pstmt.setString(7, j.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Jadwal j) {
        String sql = "UPDATE jadwal SET kereta_id = ?, stasiun_awal_id = ?, stasiun_tujuan_id = ?, " +
                     "harga_tiket = ?, waktu_berangkat = ?, waktu_tiba = ?, status = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, j.getKeretaId());
            pstmt.setInt(2, j.getStasiunAwalId());
            pstmt.setInt(3, j.getStasiunTujuanId());
            pstmt.setInt(4, j.getHargaTiket());
            pstmt.setTimestamp(5, j.getWaktuBerangkat());
            pstmt.setTimestamp(6, j.getWaktuTiba());
            pstmt.setString(7, j.getStatus());
            pstmt.setInt(8, j.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM jadwal WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Jadwal> search(String keyword) {
        List<Jadwal> list = new ArrayList<>();
        String sql = "SELECT j.*, k.nama_kereta, s1.nama_stasiun as awal, s2.nama_stasiun as tujuan " +
                     "FROM jadwal j " +
                     "JOIN kereta k ON j.kereta_id = k.id " +
                     "JOIN stasiun s1 ON j.stasiun_awal_id = s1.id " +
                     "JOIN stasiun s2 ON j.stasiun_tujuan_id = s2.id " +
                     "WHERE k.nama_kereta LIKE ? OR s1.nama_stasiun LIKE ? OR s2.nama_stasiun LIKE ? " +
                     "ORDER BY j.id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String term = "%" + keyword + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToJadwal(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Jadwal mapResultSetToJadwal(ResultSet rs) throws SQLException {
        Jadwal j = new Jadwal();
        j.setId(rs.getInt("id"));
        j.setKeretaId(rs.getInt("kereta_id"));
        j.setStasiunAwalId(rs.getInt("stasiun_awal_id"));
        j.setStasiunTujuanId(rs.getInt("stasiun_tujuan_id"));
        j.setHargaTiket(rs.getInt("harga_tiket"));
        j.setWaktuBerangkat(rs.getTimestamp("waktu_berangkat"));
        j.setWaktuTiba(rs.getTimestamp("waktu_tiba"));
        j.setStatus(rs.getString("status"));
        j.setCreatedAt(rs.getTimestamp("created_at"));
        j.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Join results
        j.setNamaKereta(rs.getString("nama_kereta"));
        j.setNamaStasiunAwal(rs.getString("awal"));
        j.setNamaStasiunTujuan(rs.getString("tujuan"));
        return j;
    }
}

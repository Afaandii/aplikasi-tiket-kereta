package org.example.dao;

import org.example.model.Jadwal;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalDAO {

    public List<Jadwal> getAll() {
        List<Jadwal> list = new ArrayList<>();
        String sql = "SELECT j.*, k.nama_kereta, s1.nama_stasiun as nama_asal, s2.nama_stasiun as nama_tujuan " +
                "FROM jadwal j " +
                "JOIN kereta k ON j.kereta_id = k.id " +
                "JOIN stasiun s1 ON j.stasiun_asal_id = s1.id " +
                "JOIN stasiun s2 ON j.stasiun_tujuan_id = s2.id " +
                "ORDER BY j.waktu_berangkat ASC";

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToJadwal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Jadwal> search(String keyword) {
        List<Jadwal> list = new ArrayList<>();
        String sql = "SELECT j.*, k.nama_kereta, s1.nama_stasiun as nama_asal, s2.nama_stasiun as nama_tujuan " +
                "FROM jadwal j " +
                "JOIN kereta k ON j.kereta_id = k.id " +
                "JOIN stasiun s1 ON j.stasiun_asal_id = s1.id " +
                "JOIN stasiun s2 ON j.stasiun_tujuan_id = s2.id " +
                "WHERE k.nama_kereta LIKE ? OR s1.nama_stasiun LIKE ? OR s2.nama_stasiun LIKE ? " +
                "ORDER BY j.waktu_berangkat DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            pstmt.setString(1, kw);
            pstmt.setString(2, kw);
            pstmt.setString(3, kw);

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

    public int insert(Jadwal j) {
        String sql = "INSERT INTO jadwal (kereta_id, stasiun_asal_id, stasiun_tujuan_id, waktu_berangkat, waktu_tiba, status, created_at, updated_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, j.getKeretaId());
            pstmt.setInt(2, j.getStasiunAsalId());
            pstmt.setInt(3, j.getStasiunTujuanId());
            pstmt.setTimestamp(4, j.getWaktuBerangkat());
            pstmt.setTimestamp(5, j.getWaktuTiba());
            pstmt.setString(6, j.getStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next())
                        return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(Jadwal j) {
        String sql = "UPDATE jadwal SET kereta_id = ?, stasiun_asal_id = ?, stasiun_tujuan_id = ?, waktu_berangkat = ?, waktu_tiba = ?, status = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, j.getKeretaId());
            pstmt.setInt(2, j.getStasiunAsalId());
            pstmt.setInt(3, j.getStasiunTujuanId());
            pstmt.setTimestamp(4, j.getWaktuBerangkat());
            pstmt.setTimestamp(5, j.getWaktuTiba());
            pstmt.setString(6, j.getStatus());
            pstmt.setInt(7, j.getId());

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

    private Jadwal mapResultSetToJadwal(ResultSet rs) throws SQLException {
        Jadwal j = new Jadwal();
        j.setId(rs.getInt("id"));
        j.setKeretaId(rs.getInt("kereta_id"));
        j.setStasiunAsalId(rs.getInt("stasiun_asal_id"));
        j.setStasiunTujuanId(rs.getInt("stasiun_tujuan_id"));
        j.setWaktuBerangkat(rs.getTimestamp("waktu_berangkat"));
        j.setWaktuTiba(rs.getTimestamp("waktu_tiba"));
        j.setStatus(rs.getString("status"));
        j.setCreatedAt(rs.getTimestamp("created_at"));
        j.setUpdatedAt(rs.getTimestamp("updated_at"));
        j.setNamaKereta(rs.getString("nama_kereta"));
        j.setNamaStasiunAsal(rs.getString("nama_asal"));
        j.setNamaStasiunTujuan(rs.getString("nama_tujuan"));
        return j;
    }
}

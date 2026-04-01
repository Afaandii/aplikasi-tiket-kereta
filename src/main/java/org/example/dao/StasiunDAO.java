package org.example.dao;

import org.example.model.Stasiun;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StasiunDAO {

    public List<Stasiun> getAll() {
        List<Stasiun> list = new ArrayList<>();
        String sql = "SELECT * FROM stasiun ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Stasiun s = new Stasiun();
                s.setId(rs.getInt("id"));
                s.setKodeStasiun(rs.getString("kode_stasiun"));
                s.setNamaStasiun(rs.getString("nama_stasiun"));
                s.setKota(rs.getString("kota"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Stasiun s) {
        String sql = "INSERT INTO stasiun (kode_stasiun, nama_stasiun, kota, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getKodeStasiun());
            pstmt.setString(2, s.getNamaStasiun());
            pstmt.setString(3, s.getKota());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Stasiun s) {
        String sql = "UPDATE stasiun SET kode_stasiun = ?, nama_stasiun = ?, kota = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getKodeStasiun());
            pstmt.setString(2, s.getNamaStasiun());
            pstmt.setString(3, s.getKota());
            pstmt.setInt(4, s.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM stasiun WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Stasiun> search(String keyword) {
        List<Stasiun> list = new ArrayList<>();
        String sql = "SELECT * FROM stasiun WHERE kode_stasiun LIKE ? OR nama_stasiun LIKE ? OR kota LIKE ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Stasiun s = new Stasiun();
                    s.setId(rs.getInt("id"));
                    s.setKodeStasiun(rs.getString("kode_stasiun"));
                    s.setNamaStasiun(rs.getString("nama_stasiun"));
                    s.setKota(rs.getString("kota"));
                    s.setCreatedAt(rs.getTimestamp("created_at"));
                    s.setUpdatedAt(rs.getTimestamp("updated_at"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

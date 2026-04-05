package org.example.dao;

import org.example.model.DetailTransaksi;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailTransaksiDAO {

    public List<DetailTransaksi> getByTransaksiId(int transaksiId) {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi WHERE transaksi_id = ? ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transaksiId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetailTransaksi dt = new DetailTransaksi();
                    dt.setId(rs.getInt("id"));
                    dt.setTransaksiId(rs.getInt("transaksi_id"));
                    dt.setJadwalKursiId(rs.getInt("jadwal_kursi_id"));
                    dt.setHarga(rs.getInt("harga"));
                    dt.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(dt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

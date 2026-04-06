package org.example.dao;

import org.example.model.Transaksi;
import org.example.model.DetailTransaksi;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    public boolean saveFullTransaction(Transaksi t, List<DetailTransaksi> details) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Transaksi
            String sqlT = "INSERT INTO transaksi (user_id, kode_transaksi, kode_booking, nama_customer, total_bayar, jumlah_tiket, status, metode_pembayaran, created_at, updated_at) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            int transaksiId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlT, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, t.getUserId());
                pstmt.setString(2, t.getKodeTransaksi());
                pstmt.setString(3, t.getKodeBooking());
                pstmt.setString(4, t.getNamaCustomer());
                pstmt.setLong(5, t.getTotalBayar());
                pstmt.setInt(6, t.getJumlahTiket());
                pstmt.setString(7, t.getStatus());
                pstmt.setString(8, t.getMetodePembayaran());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) transaksiId = rs.getInt(1);
                }
            }

            if (transaksiId == -1) throw new SQLException("Failed to get transaction ID");

            // 2. Insert Details & Update Seat Status
            String sqlD = "INSERT INTO detail_transaksi (transaksi_id, jadwal_kursi_id, harga, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
            String sqlS = "UPDATE jadwal_kursi SET status = 'dipesan', updated_at = NOW() WHERE id = ?";
            
            try (PreparedStatement pstmtD = conn.prepareStatement(sqlD);
                 PreparedStatement pstmtS = conn.prepareStatement(sqlS)) {
                
                for (DetailTransaksi dt : details) {
                    // Save Detail
                    pstmtD.setInt(1, transaksiId);
                    pstmtD.setInt(2, dt.getJadwalKursiId());
                    pstmtD.setString(3, String.valueOf(dt.getHarga())); // Changed to String to match VARCHAR column
                    pstmtD.addBatch();

                    // Update Seat
                    pstmtS.setInt(1, dt.getJadwalKursiId());
                    pstmtS.addBatch();
                }
                pstmtD.executeBatch();
                pstmtS.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                java.nio.file.Files.writeString(java.nio.file.Path.of("error_log.txt"), 
                    "\n--- DATABASE ERROR REPORT ---\n" +
                    "Time: " + new java.util.Date() + "\n" +
                    "Error: " + e.getMessage() + "\n\n", 
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                
                // Diagnostic: Check 'transaksi' structure
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("DESCRIBE transaksi")) {
                    java.nio.file.Files.writeString(java.nio.file.Path.of("error_log.txt"), "Table: transaksi\n", java.nio.file.StandardOpenOption.APPEND);
                    while(rs.next()) {
                        java.nio.file.Files.writeString(java.nio.file.Path.of("error_log.txt"), 
                            String.format("- %s (%s)\n", rs.getString("Field"), rs.getString("Type")), 
                            java.nio.file.StandardOpenOption.APPEND);
                    }
                }
                
                // Diagnostic: Check 'jadwal_kursi' structure
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("DESCRIBE jadwal_kursi")) {
                    java.nio.file.Files.writeString(java.nio.file.Path.of("error_log.txt"), "\nTable: jadwal_kursi\n", java.nio.file.StandardOpenOption.APPEND);
                    while(rs.next()) {
                        java.nio.file.Files.writeString(java.nio.file.Path.of("error_log.txt"), 
                            String.format("- %s (%s)\n", rs.getString("Field"), rs.getString("Type")), 
                            java.nio.file.StandardOpenOption.APPEND);
                    }
                }

                try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("error_log.txt", true))) {
                    e.printStackTrace(pw);
                }
            } catch (java.io.IOException | SQLException logEx) {
                logEx.printStackTrace();
            }
            
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public List<Transaksi> getByUserId(int userId) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaksi t = new Transaksi();
                    t.setId(rs.getInt("id"));
                    t.setKodeTransaksi(rs.getString("kode_transaksi"));
                    t.setKodeBooking(rs.getString("kode_booking"));
                    t.setNamaCustomer(rs.getString("nama_customer"));
                    t.setTotalBayar(rs.getLong("total_bayar"));
                    t.setJumlahTiket(rs.getInt("jumlah_tiket"));
                    t.setStatus(rs.getString("status"));
                    t.setMetodePembayaran(rs.getString("metode_pembayaran"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

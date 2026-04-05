package org.example.dao;

import org.example.utils.Database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardDAO {

    public Map<String, Object> getSummaryStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count queries
        stats.put("total_stasiun", getCount("stasiun"));
        stats.put("total_kereta", getCount("kereta"));
        stats.put("total_jadwal", getCount("jadwal"));
        stats.put("total_gerbong", getCount("gerbong"));
        stats.put("total_user", getCount("user"));
        
        // Sum query for revenue
        stats.put("total_pendapatan", getSum("transaksi", "total_bayar"));
        
        return stats;
    }

    private int getCount(String table) {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long getSum(String table, String column) {
        String sql = "SELECT SUM(" + column + ") FROM " + table;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

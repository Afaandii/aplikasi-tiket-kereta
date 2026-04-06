package org.example.utils;

import java.sql.*;

public class Diagnostic {
    public static void main(String[] args) {
        try (Connection conn = Database.getConnection()) {
            System.out.println("----- DIAGNOSTIC START -----");
            checkTable(conn, "transaksi");
            checkTable(conn, "detail_transaksi");
            checkTable(conn, "jadwal_kursi");
            System.out.println("----- DIAGNOSTIC END -----");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkTable(Connection conn, String tableName) throws SQLException {
        System.out.println("\nTable Information for: " + tableName);
        String sql = "DESCRIBE " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println(String.format("%-20s %-15s %-10s %-10s %-10s %-15s", 
                "Field", "Type", "Null", "Key", "Default", "Extra"));
            System.out.println("---------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.println(String.format("%-20s %-15s %-10s %-10s %-10s %-15s",
                    rs.getString("Field"),
                    rs.getString("Type"),
                    rs.getString("Null"),
                    rs.getString("Key"),
                    rs.getString("Default"),
                    rs.getString("Extra")));
            }
        }
    }
}

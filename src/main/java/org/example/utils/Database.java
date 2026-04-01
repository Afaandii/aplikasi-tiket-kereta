package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/db_aplikasi_penjualan_tiket_kereta";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Kosongkan jika tidak ada password
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Koneksi Database Berhasil!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Koneksi Database Gagal: " + e.getMessage());
        }
        return connection;
    }
}

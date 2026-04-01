package org.example.dao;

import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KelasKeretaDAO {

    public static class Kelas {
        public int id;
        public String namaKelas;
        public Kelas(int id, String namaKelas) { this.id = id; this.namaKelas = namaKelas; }
        @Override
        public String toString() { return namaKelas; }
    }

    public List<Kelas> getAll() {
        List<Kelas> list = new ArrayList<>();
        String sql = "SELECT * FROM kelas_kereta ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Kelas(rs.getInt("id"), rs.getString("nama_kelas")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

package org.example.dao;

import org.example.model.Role;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<Role> getAll() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM role ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Try 'name' column, fallback to 'nama' or 'nama_role' if needed
                int id = rs.getInt("id");
                String name = rs.getString("nama_role");
                list.add(new Role(id, name));
            }
        } catch (SQLException e) {
            System.err.println("RoleDAO getAll() error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}

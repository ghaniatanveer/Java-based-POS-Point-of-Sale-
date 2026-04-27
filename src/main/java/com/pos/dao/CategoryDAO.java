package com.pos.dao;

import com.pos.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT id, name FROM categories ORDER BY name";
        List<Category> data = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return data;
    }
}

package com.pos.dao;

import com.pos.models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT id, name, barcode, category_id, price, stock_qty FROM products ORDER BY id DESC";
        List<Product> data = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.add(map(rs));
            }
        }
        return data;
    }

    public List<Product> search(String keyword) throws SQLException {
        String sql = """
                SELECT id, name, barcode, category_id, price, stock_qty
                FROM products
                WHERE LOWER(name) LIKE LOWER(?) OR LOWER(barcode) LIKE LOWER(?)
                ORDER BY name
                """;
        List<Product> data = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(map(rs));
            }
        }
        return data;
    }

    public void save(Product p) throws SQLException {
        String sql = "INSERT INTO products(name, barcode, category_id, price, stock_qty) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getBarcode());
            ps.setInt(3, p.getCategoryId());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStockQty());
            ps.executeUpdate();
        }
    }

    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, barcode=?, category_id=?, price=?, stock_qty=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getBarcode());
            ps.setInt(3, p.getCategoryId());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStockQty());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("barcode"),
                rs.getInt("category_id"),
                rs.getDouble("price"),
                rs.getInt("stock_qty")
        );
    }
}

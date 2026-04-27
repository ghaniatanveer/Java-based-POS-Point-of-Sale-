package com.pos.dao;

import com.pos.models.Sale;
import com.pos.models.SaleItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {
    public int save(Sale sale) throws SQLException {
        String saleSql = """
                INSERT INTO sales(customer_id, cashier_user_id, created_at, subtotal, discount, tax, total)
                VALUES(?,?,?,?,?,?,?)
                """;
        String itemSql = "INSERT INTO sale_items(sale_id, product_id, quantity, unit_price, line_total) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psSale = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                if (sale.getCustomerId() == null) {
                    psSale.setNull(1, Types.INTEGER);
                } else {
                    psSale.setInt(1, sale.getCustomerId());
                }
                psSale.setInt(2, sale.getCashierUserId());
                psSale.setTimestamp(3, Timestamp.valueOf(sale.getCreatedAt()));
                psSale.setDouble(4, sale.getSubtotal());
                psSale.setDouble(5, sale.getDiscount());
                psSale.setDouble(6, sale.getTax());
                psSale.setDouble(7, sale.getTotal());
                psSale.executeUpdate();
                ResultSet keys = psSale.getGeneratedKeys();
                if (keys.next()) {
                    int saleId = keys.getInt(1);
                    try (PreparedStatement psItem = conn.prepareStatement(itemSql);
                         PreparedStatement psStock = conn.prepareStatement("UPDATE products SET stock_qty = stock_qty - ? WHERE id = ?")) {
                        for (SaleItem item : sale.getItems()) {
                            psItem.setInt(1, saleId);
                            psItem.setInt(2, item.getProductId());
                            psItem.setInt(3, item.getQuantity());
                            psItem.setDouble(4, item.getUnitPrice());
                            psItem.setDouble(5, item.getLineTotal());
                            psItem.addBatch();

                            psStock.setInt(1, item.getQuantity());
                            psStock.setInt(2, item.getProductId());
                            psStock.addBatch();
                        }
                        psItem.executeBatch();
                        psStock.executeBatch();
                    }
                    conn.commit();
                    return saleId;
                }
                throw new SQLException("Sale insert failed");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Sale> findByDateRange(LocalDate from, LocalDate to) throws SQLException {
        LocalDate safeFrom = from == null ? LocalDate.now().minusDays(7) : from;
        LocalDate safeTo = to == null ? LocalDate.now() : to;
        if (safeFrom.isAfter(safeTo)) {
            LocalDate temp = safeFrom;
            safeFrom = safeTo;
            safeTo = temp;
        }

        String sql = """
                SELECT id, customer_id, cashier_user_id, created_at, subtotal, discount, tax, total
                FROM sales
                WHERE CAST(created_at AS DATE) BETWEEN ? AND ?
                ORDER BY created_at DESC
                """;
        List<Sale> data = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(safeFrom));
            ps.setDate(2, Date.valueOf(safeTo));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Sale s = new Sale();
                s.setId(rs.getInt("id"));
                int customerId = rs.getInt("customer_id");
                s.setCustomerId(rs.wasNull() ? null : customerId);
                s.setCashierUserId(rs.getInt("cashier_user_id"));
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                s.setSubtotal(rs.getDouble("subtotal"));
                s.setDiscount(rs.getDouble("discount"));
                s.setTax(rs.getDouble("tax"));
                s.setTotal(rs.getDouble("total"));
                data.add(s);
            }
        }
        return data;
    }

    /**
     * Deletes a sale and its line items, restoring product stock quantities.
     */
    public void deleteSaleAndRestoreStock(int saleId) throws SQLException {
        String selectItems = "SELECT product_id, quantity FROM sale_items WHERE sale_id = ?";
        String deleteItems = "DELETE FROM sale_items WHERE sale_id = ?";
        String deleteSale = "DELETE FROM sales WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psSel = conn.prepareStatement(selectItems);
                     PreparedStatement psStock = conn.prepareStatement("UPDATE products SET stock_qty = stock_qty + ? WHERE id = ?")) {
                    psSel.setInt(1, saleId);
                    try (ResultSet rs = psSel.executeQuery()) {
                        while (rs.next()) {
                            int productId = rs.getInt("product_id");
                            int qty = rs.getInt("quantity");
                            psStock.setInt(1, qty);
                            psStock.setInt(2, productId);
                            psStock.executeUpdate();
                        }
                    }
                }
                try (PreparedStatement psDelItems = conn.prepareStatement(deleteItems)) {
                    psDelItems.setInt(1, saleId);
                    psDelItems.executeUpdate();
                }
                try (PreparedStatement psDelSale = conn.prepareStatement(deleteSale)) {
                    psDelSale.setInt(1, saleId);
                    int deleted = psDelSale.executeUpdate();
                    if (deleted == 0) {
                        conn.rollback();
                        throw new SQLException("Sale not found: " + saleId);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}

package com.retail;

import java.sql.*;

public class ProductDAO {

    private final Connection con;

    public ProductDAO(Connection con) {
        this.con = con;
    }

    // (a) Insert using PreparedStatement, duplicate ID handled
    public void insertProduct(int id, String name, String category, int qty, double price) {
        String sql = "INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, category);
            ps.setInt(4, qty);
            ps.setDouble(5, price);
            ps.executeUpdate();
            System.out.println("Product " + id + " inserted successfully.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: Product ID " + id + " already exists. Record skipped.");
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
    }

    // (b) Category filter, quantity < 20, sorted by price DESC
    public void showLowStockByCategory(String category) {
        String sql = "SELECT * FROM Product WHERE category = ? AND quantity < 20 ORDER BY price DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                printHeader();
                while (rs.next()) {
                    found = true;
                    printRow(rs);
                }
                if (!found) {
                    System.out.println("No products in '" + category + "' with quantity less than 20.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    // (c) Add to existing stock
    public void addStock(int id, int addQty) {
        String sql = "UPDATE Product SET quantity = quantity + ? WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, addQty);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Stock updated. Added " + addQty + " units to product " + id + ".");
            } else {
                System.out.println("Product with ID " + id + " does not exist.");
            }
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    // (d) Search, then update price only if product exists and newPrice > 0
    public void updatePrice(int id, double newPrice) {
        String select = "SELECT * FROM Product WHERE product_id = ?";
        String update = "UPDATE Product SET price = ? WHERE product_id = ?";
        try (PreparedStatement sel = con.prepareStatement(select)) {
            sel.setInt(1, id);
            try (ResultSet rs = sel.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Product with ID " + id + " does not exist.");
                    return;
                }
                System.out.println("Current details:");
                printHeader();
                printRow(rs);
            }

            if (newPrice <= 0) {
                System.out.println("Invalid price. New price must be greater than zero. No update done.");
                return;
            }

            try (PreparedStatement upd = con.prepareStatement(update)) {
                upd.setDouble(1, newPrice);
                upd.setInt(2, id);
                upd.executeUpdate();
            }

            try (PreparedStatement sel2 = con.prepareStatement(select)) {
                sel2.setInt(1, id);
                try (ResultSet rs2 = sel2.executeQuery()) {
                    System.out.println("Updated details:");
                    printHeader();
                    if (rs2.next()) {
                        printRow(rs2);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    // (e) Total inventory value
    public void showTotalInventoryValue() {
        String sql = "SELECT SUM(quantity * price) AS total FROM Product";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                System.out.printf("Total inventory value: %.2f%n", rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    // (e) Delete only if quantity is zero
    public void deleteIfZeroStock(int id) {
        String select = "SELECT quantity FROM Product WHERE product_id = ?";
        String delete = "DELETE FROM Product WHERE product_id = ?";
        try (PreparedStatement sel = con.prepareStatement(select)) {
            sel.setInt(1, id);
            try (ResultSet rs = sel.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Product with ID " + id + " does not exist.");
                    return;
                }
                int qty = rs.getInt("quantity");
                if (qty != 0) {
                    System.out.println("Cannot delete: product " + id + " still has " + qty + " units in stock.");
                    return;
                }
            }
            try (PreparedStatement del = con.prepareStatement(delete)) {
                del.setInt(1, id);
                del.executeUpdate();
                System.out.println("Product " + id + " deleted successfully (quantity was zero).");
            }
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    // Extra helper: view everything
    public void showAll() {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Product ORDER BY product_id")) {
            printHeader();
            boolean found = false;
            while (rs.next()) {
                found = true;
                printRow(rs);
            }
            if (!found) {
                System.out.println("(no records)");
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    private void printHeader() {
        System.out.printf("%-6s %-20s %-15s %-8s %-10s%n", "ID", "Name", "Category", "Qty", "Price");
        System.out.println("-------------------------------------------------------------");
    }

    private void printRow(ResultSet rs) throws SQLException {
        System.out.printf("%-6d %-20s %-15s %-8d %-10.2f%n",
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("price"));
    }
}

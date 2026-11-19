package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// All database work (SQL) lives here
public class OrderDAO {

    // (a) Insert one order. Returns true if inserted, false if duplicate/error.
    public boolean insertOrder(Order o) {
        String sql = "INSERT INTO Orders (order_id, customer_name, product_name, quantity, unit_price, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getOrderId());
            ps.setString(2, o.getCustomerName());
            ps.setString(3, o.getProductName());
            ps.setInt(4, o.getQuantity());
            ps.setDouble(5, o.getUnitPrice());
            ps.setString(6, o.getStatus());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {          // 1062 = duplicate primary key
                System.out.println("Order ID " + o.getOrderId() + " already exists. Skipped.");
            } else {
                System.out.println("Insert failed: " + e.getMessage());
            }
            return false;
        }
    }

    // (b) Orders with a given status and quantity > 2, highest unit price first
    public List<Order> getOrdersByStatus(String status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE status = ? AND quantity > 2 ORDER BY unit_price DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return list;
    }

    // (c) Increase quantity of an existing order. Returns rows affected (0 = not found)
    public int addQuantity(int orderId, int extraQty) {
        String sql = "UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, extraQty);
            ps.setInt(2, orderId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return 0;
        }
    }

    // Find one order by ID, null if it does not exist
    public Order findById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
        return null;
    }

    // (d) Update the status of an order. Returns rows affected
    public int updateStatus(int orderId, String newStatus) {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return 0;
        }
    }

    // (e1) Total order value = SUM(quantity * unit_price)
    public double getTotalOrderValue() {
        String sql = "SELECT SUM(quantity * unit_price) AS total FROM Orders";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");       // 0 if table is empty (NULL)
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return 0;
    }

    // (e2) Delete only if status is Cancelled. Returns a message describing what happened
    public String deleteIfCancelled(int orderId) {
        Order o = findById(orderId);
        if (o == null) {
            return "Order does not exist.";
        }
        if (!"Cancelled".equalsIgnoreCase(o.getStatus())) {
            return "Cannot delete: order status is '" + o.getStatus() + "'. Only Cancelled orders can be deleted.";
        }
        String sql = "DELETE FROM Orders WHERE order_id = ? AND LOWER(status) = 'cancelled'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            int rows = ps.executeUpdate();
            return rows > 0 ? "Order deleted successfully." : "Delete did not happen.";
        } catch (SQLException e) {
            return "Delete failed: " + e.getMessage();
        }
    }

    // Converts one ResultSet row into an Order object
    private Order mapRow(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("order_id"),
                rs.getString("customer_name"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getDouble("unit_price"),
                rs.getString("status"));
    }
}
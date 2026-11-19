package org.example;

// Model class: one row of the Orders table
public class Order {
    private int orderId;
    private String customerName;
    private String productName;
    private int quantity;
    private double unitPrice;
    private String status;

    public Order(int orderId, String customerName, String productName,
                 int quantity, double unitPrice, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("Order ID: %d | Customer: %s | Product: %s | Qty: %d | Unit Price: %.2f | Status: %s",
                orderId, customerName, productName, quantity, unitPrice, status);
    }
}
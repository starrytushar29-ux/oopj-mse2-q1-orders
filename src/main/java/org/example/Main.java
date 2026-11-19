package org.example;

import java.util.List;
import java.util.Scanner;

// Menu-driven console application
public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final OrderDAO dao = new OrderDAO();
    // Allowed statuses (a collection used for validation)
    private static final List<String> VALID_STATUSES = List.of("Pending", "Shipped", "Delivered", "Cancelled");

    public static void main(String[] args) {
        int choice;
        do {
            printMenu();
            choice = readInt("Enter choice: ");
            switch (choice) {
                case 1 -> insertSampleOrders();
                case 2 -> showOrdersByStatus();
                case 3 -> increaseQuantity();
                case 4 -> changeStatus();
                case 5 -> totalValueAndDelete();
                case 6 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice. Please enter 1-6.");
            }
        } while (choice != 6);
    }

    private static void printMenu() {
        System.out.println("\n===== Order Management System =====");
        System.out.println("1. Insert sample orders");
        System.out.println("2. Show orders by status (quantity > 2)");
        System.out.println("3. Increase order quantity");
        System.out.println("4. Update order status");
        System.out.println("5. Total order value / delete cancelled order");
        System.out.println("6. Exit");
    }

    // (a) Insert at least three orders
    private static void insertSampleOrders() {
        Order[] samples = {
                new Order(1, "Ravi Kumar", "Laptop", 3, 55000, "Pending"),
                new Order(2, "Anita Singh", "Mouse", 5, 500, "Pending"),
                new Order(3, "Mohit Sharma", "Keyboard", 1, 1200, "Shipped"),
                new Order(4, "Priya Verma", "Monitor", 4, 12000, "Cancelled")
        };
        for (Order o : samples) {
            if (dao.insertOrder(o)) {
                System.out.println("Inserted order " + o.getOrderId());
            }
        }
    }

    // (b) Orders by status with quantity > 2, sorted by unit price (high to low)
    private static void showOrdersByStatus() {
        System.out.print("Enter order status: ");
        String status = sc.nextLine().trim();
        List<Order> list = dao.getOrdersByStatus(status);
        if (list.isEmpty()) {
            System.out.println("No orders with status '" + status + "' and quantity greater than 2.");
        } else {
            list.forEach(System.out::println);
        }
    }

    // (c) Increase quantity
    private static void increaseQuantity() {
        int id = readInt("Enter order ID: ");
        int extra = readInt("Enter additional quantity: ");
        if (extra <= 0) {
            System.out.println("Additional quantity must be greater than zero.");
            return;
        }
        int rows = dao.addQuantity(id, extra);
        System.out.println(rows > 0 ? "Quantity updated successfully." : "Order does not exist.");
    }

    // (d) Search, validate the new status, then update
    private static void changeStatus() {
        int id = readInt("Enter order ID: ");
        System.out.print("Enter new status (Pending / Shipped / Delivered / Cancelled): ");
        String input = sc.nextLine().trim();

        Order existing = dao.findById(id);
        if (existing == null) {
            System.out.println("Order does not exist.");
            return;
        }
        String validStatus = null;                 // canonical spelling of the status
        for (String s : VALID_STATUSES) {
            if (s.equalsIgnoreCase(input)) {
                validStatus = s;
                break;
            }
        }
        if (validStatus == null) {
            System.out.println("Invalid status. Allowed: " + VALID_STATUSES);
            return;
        }
        dao.updateStatus(id, validStatus);
        System.out.println("Updated order details:");
        System.out.println(dao.findById(id));
    }

    // (e) Aggregate + conditional delete
    private static void totalValueAndDelete() {
        System.out.printf("Total order value: %.2f%n", dao.getTotalOrderValue());
        int id = readInt("Enter order ID to delete (only Cancelled orders can be deleted): ");
        System.out.println(dao.deleteIfCancelled(id));
    }

    // Safe input helper: keeps asking until a valid whole number is typed
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
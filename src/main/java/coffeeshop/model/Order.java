package coffeeshop.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static int nextOrderId = 1000;
    private int orderId;
    private String customerId;
    private LocalDateTime timestamp;
    private List<MenuItem> items;
    private double totalAmount;
    private boolean isProcessed;
    private boolean isOnline; // New field to track online orders

    public Order(String customerId) {
        this(customerId, false); // Default to in-store order
    }

    // New constructor with online parameter
    public Order(String customerId, boolean isOnline) {
        this.orderId = nextOrderId++;
        this.customerId = customerId;
        this.timestamp = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
        this.isProcessed = false;
        this.isOnline = isOnline;
    }

    // Updated constructor for loading existing orders
    public Order(int orderId, String customerId, LocalDateTime timestamp, boolean isProcessed, boolean isOnline) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.timestamp = timestamp;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
        this.isProcessed = isProcessed;
        this.isOnline = isOnline;

        // Update next order ID if needed
        if (orderId >= nextOrderId) {
            nextOrderId = orderId + 1;
        }
    }

    public void addItem(MenuItem item) {
        items.add(item);
        totalAmount += item.getPrice();
    }

    // Apply discount - simplified version
    public void applyDiscount(double discountPercent) {
        if (discountPercent > 0 && discountPercent <= 100) {
            totalAmount = totalAmount * (1 - (discountPercent / 100));
        }
    }

    // Getters
    public int getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<MenuItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public boolean isProcessed() { return isProcessed; }
    public boolean isOnline() { return isOnline; } // New getter

    // Setters
    public void setProcessed(boolean processed) { this.isProcessed = processed; }
    public void setOnline(boolean online) { this.isOnline = online; } // New setter

    @Override
    public String toString() {
        return "Order #" + orderId + " - Customer: " + customerId +
                " - Items: " + items.size() + " - Total: $" + String.format("%.2f", totalAmount) +
                (isOnline ? " (Online)" : " (In-store)");
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
}
package coffeeshop.simulation;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.util.FileManager;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;

public class CoffeeShopSimulation {
    // Changed to PriorityBlockingQueue with a comparator that prioritizes online orders
    private BlockingQueue<Order> orderQueue = new PriorityBlockingQueue<>(20, (o1, o2) -> {
        // Online orders get priority over in-store orders
        if (o1.isOnline() != o2.isOnline()) {
            return o1.isOnline() ? -1 : 1;  // Online orders come first
        }
        // For orders of the same type (both online or both in-store), use timestamp (FIFO)
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    });

    private final int MAX_QUEUE_SIZE = 20; // Optional: define as constant
    private List<StaffMember> staff;
    private List<Order> completedOrders;
    private Map<String, MenuItem> menu;
    private boolean isRunning;
    private ExecutorService executor;
    private DiscountManager discountManager;
    private List<SimulationObserver> observers;

    // Constructor
    public CoffeeShopSimulation() {
        this.staff = new ArrayList<>();
        this.completedOrders = new ArrayList<>();
        this.menu = FileManager.loadMenu();
        this.isRunning = false;
        this.discountManager = new DiscountManager();
        this.observers = new ArrayList<>();
    }

    // Initialize simulation with existing orders
    public void initialize() {
        List<Order> existingOrders = FileManager.loadOrders(menu);
        for (Order order : existingOrders) {
            orderQueue.add(order);
        }

        FileManager.logEvent("Simulation initialized with " + existingOrders.size() + " existing orders");
    }

    // Start the simulation
    public void start(int staffCount) {
        if (isRunning) return;

        isRunning = true;
        executor = Executors.newFixedThreadPool(staffCount);

        // Create staff members
        for (int i = 1; i <= staffCount; i++) {
            StaffMember staffMember = new StaffMember("Staff " + i, this);
            staff.add(staffMember);
            executor.execute(staffMember);
        }

        FileManager.logEvent("Simulation started with " + staffCount + " staff members");
        notifyObservers();
    }

    // Stop the simulation
    public void stop() {
        if (!isRunning) return;

        isRunning = false;

        // Stop all staff
        for (StaffMember staffMember : staff) {
            staffMember.stopWorking();
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        FileManager.logEvent("Simulation stopped");
        generateReport();
    }

    // Add a new order to the queue
    public void addOrder(Order order) {
        try {
            if (orderQueue.size() < MAX_QUEUE_SIZE) {
                discountManager.applyDiscount(order);
                orderQueue.put(order);
                FileManager.logEvent("Order #" + order.getOrderId() +
                        (order.isOnline() ? " (Online)" : " (In-store)") + " added to queue");
                notifyObservers();
            } else {
                FileManager.logEvent("Queue full! Order #" + order.getOrderId() + " rejected");
                notifyObservers();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            FileManager.logEvent("Error adding order: " + e.getMessage());
        }
    }

    public int getMaxQueueSize() {
        return MAX_QUEUE_SIZE;
    }

    // Get next order from the queue
    public synchronized Order getNextOrder() {
        try {
            return orderQueue.poll(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    // Order has been completed
    public synchronized void orderCompleted(Order order) {
        completedOrders.add(order);
        notifyObservers();
    }

    // Update staff status
    public void updateStaffStatus(StaffMember staff, String status) {
        notifyObservers();
    }

    public synchronized void addStaffMember() {
        StaffMember staffMember = new StaffMember("Staff " + (staff.size()+1), this);
        staff.add(staffMember);
        executor.execute(staffMember);
        FileManager.logEvent("Added staff member: " + staffMember.getName());
    }

    public synchronized void removeStaffMember() {
        if (!staff.isEmpty()) {
            StaffMember member = staff.remove(staff.size()-1);
            member.stopWorking();
            FileManager.logEvent("Removed staff member: " + member.getName());
        }
    }

    // Generate report with online order statistics
    private void generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("===== COFFEE SHOP SIMULATION REPORT =====\n\n");
        report.append("Time: ").append(new Date()).append("\n\n");

        report.append("ORDERS SUMMARY:\n");
        report.append("Total orders processed: ").append(completedOrders.size()).append("\n");

        // Count online vs in-store orders
        long onlineOrders = completedOrders.stream().filter(Order::isOnline).count();
        long inStoreOrders = completedOrders.size() - onlineOrders;

        report.append("Online orders: ").append(onlineOrders).append("\n");
        report.append("In-store orders: ").append(inStoreOrders).append("\n");

        double totalRevenue = 0;
        double onlineRevenue = 0;
        double inStoreRevenue = 0;
        Map<String, Integer> itemsSold = new HashMap<>();
        Map<String, Integer> customerOrders = new HashMap<>();

        for (Order order : completedOrders) {
            double orderAmount = order.getTotalAmount();
            totalRevenue += orderAmount;

            if (order.isOnline()) {
                onlineRevenue += orderAmount;
            } else {
                inStoreRevenue += orderAmount;
            }

            String customerId = order.getCustomerId();
            customerOrders.put(customerId, customerOrders.getOrDefault(customerId, 0) + 1);

            for (MenuItem item : order.getItems()) {
                String itemName = item.getName();
                itemsSold.put(itemName, itemsSold.getOrDefault(itemName, 0) + 1);
            }
        }

        report.append("Total revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        report.append("Online revenue: $").append(String.format("%.2f", onlineRevenue)).append("\n");
        report.append("In-store revenue: $").append(String.format("%.2f", inStoreRevenue)).append("\n\n");

        report.append("ITEMS SOLD:\n");
        for (Map.Entry<String, Integer> entry : itemsSold.entrySet()) {
            report.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        report.append("\n");

        report.append("CUSTOMER SUMMARY:\n");
        report.append("Total unique customers: ").append(customerOrders.size()).append("\n");
        report.append("Customer with most orders: ").append(getMostFrequentKey(customerOrders)).append("\n\n");

        report.append("===== END OF REPORT =====");

        String reportStr = report.toString();
        FileManager.saveReport(reportStr);
        FileManager.logEvent("Simulation report generated");
    }

    private <T> T getMostFrequentKey(Map<T, Integer> map) {
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Observer pattern methods
    public void addObserver(SimulationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(SimulationObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (SimulationObserver observer : observers) {
            observer.update(this);
        }
    }

    // Getters
    public Queue<Order> getOrderQueue() { return orderQueue; }
    public List<StaffMember> getStaff() { return staff; }
    public List<Order> getCompletedOrders() { return completedOrders; }
    public Map<String, MenuItem> getMenu() { return menu; }
    public boolean isRunning() { return isRunning; }
}
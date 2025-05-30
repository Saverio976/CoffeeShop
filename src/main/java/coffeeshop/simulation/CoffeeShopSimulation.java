package coffeeshop.simulation;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.util.FileManager;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CoffeeShopSimulation {
    private BlockingQueue<Order> orderQueue = new PriorityBlockingQueue<>(999, (o1, o2) -> {
        if (o1.isOnline() != o2.isOnline()) {
            return o1.isOnline() ? -1 : 1;  // Online orders come first
        }
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    });

    private final int MAX_QUEUE_SIZE = 999;
    private List<StaffMember> staff;
    private List<Order> completedOrders;
    private Map<String, MenuItem> menu;
    private boolean isRunning;
    private List<Thread> executor;
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

    public void initialize() {
        List<Order> existingOrders = FileManager.loadOrders(menu);
        for (Order order : existingOrders) {
            addOrder(order);
        }

        FileManager.logEvent("Simulation initialized with " + existingOrders.size() + " existing orders");
    }

    public void start(int staffCount) {
        if (isRunning) return;

        isRunning = true;
        executor = new LinkedList<Thread>();

        for (int i = 1; i <= staffCount; i++) {
            StaffMember staffMember = new StaffMember("Staff " + i, this);
            staff.add(staffMember);
            Thread t = new Thread(staffMember);
            executor.add(t);
            t.start();
        }

        FileManager.logEvent("Simulation started with " + staffCount + " staff members");
        notifyObservers();
    }

    public void stop() {
        if (!isRunning) return;

        isRunning = false;

        for (StaffMember staffMember : staff) {
            staffMember.stopWorking();
        }

        while (executor.size() != 0)
        {
            Thread t = executor.removeFirst();
            try {
                t.join(10 * 1000);
            } catch (InterruptedException e) {
                FileManager.logEvent(e.getMessage());
                executor.add(t);
            }
        }

        staff.clear();

        FileManager.logEvent("Simulation stopped");
        generateReport();
    }

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

    public synchronized Order getNextOrder() {
        try {
            return orderQueue.poll(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public synchronized void orderCompleted(Order order) {
        completedOrders.add(order);
        notifyObservers();
    }

    public void updateStaffStatus(StaffMember staff, String status) {
        notifyObservers();
    }

    public synchronized void addStaffMember() {
        StaffMember staffMember = new StaffMember("Staff " + (staff.size()+1), this);
        staff.add(staffMember);
        Thread t = new Thread(staffMember);
        executor.add(t);
        t.start();
        FileManager.logEvent("Added staff member: " + staffMember.getName());
    }

    public synchronized void removeStaffMember() {
        if (!staff.isEmpty()) {
            StaffMember member = staff.removeLast();
            member.stopWorking();
            Thread t = executor.removeLast();
            try {
                t.join(10 * 1000);
            } catch (InterruptedException e) {
                FileManager.logEvent(e.getMessage());
            }
            FileManager.logEvent("Removed staff member: " + member.getName());
        }
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("===== COFFEE SHOP SIMULATION REPORT =====\n\n");
        report.append("Time: ").append(new Date()).append("\n\n");

        report.append("ORDERS SUMMARY:\n");
        report.append("Total orders processed: ").append(completedOrders.size()).append("\n");

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

    public Queue<Order> getOrderQueue() { return orderQueue; }
    public List<StaffMember> getStaff() { return staff; }
    public List<Order> getCompletedOrders() { return completedOrders; }
    public Map<String, MenuItem> getMenu() { return menu; }
    public boolean isRunning() { return isRunning; }
}

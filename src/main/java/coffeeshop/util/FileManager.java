// FileManager.java
package coffeeshop.util;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    // Update these constants
    private static final String DATA_DIR = "data/";
    private static final String MENU_FILE = "/data/menu.csv";
    private static final String ORDERS_FILE = "/data/orders.csv";
    private static final String LOG_FILE = DATA_DIR + "coffee_shop_log.txt";
    private static final String REPORT_FILE = DATA_DIR + "coffee_shop_report.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        // Create data directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    // Load menu items from file
    public static Map<String, MenuItem> loadMenu() {
        Map<String, MenuItem> menuItems = new HashMap<>();
        InputStream menuStream = FileManager.class.getResourceAsStream(MENU_FILE);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(menuStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String description = parts[2].trim();
                    double price = Double.parseDouble(parts[3].trim());
                    String category = parts[4].trim();

                    MenuItem item = new MenuItem(id, name, description, price, category);
                    menuItems.put(id, item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading menu: " + e.getMessage());
        }

        return menuItems;
    }

    // Load existing orders from file
    public static List<Order> loadOrders(Map<String, MenuItem> menuItems) {
        List<Order> orders = new ArrayList<>();
        InputStream orderStream = FileManager.class.getResourceAsStream(ORDERS_FILE);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(orderStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) { // Updated to expect 5 parts (added isOnline flag)
                    int orderId = Integer.parseInt(parts[0].trim());
                    String customerId = parts[1].trim();
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2].trim(), DATE_FORMATTER);
                    String itemId = parts[3].trim();
                    // Parse the isOnline flag (defaults to false if not present)
                    boolean isOnline = parts.length > 4 && parts[4].trim().equalsIgnoreCase("true");

                    // Check if order already exists
                    Order order = findOrderById(orders, orderId);

                    if (order == null) {
                        order = new Order(orderId, customerId, timestamp, false, isOnline);
                        orders.add(order);
                    }

                    // Add item to order if it exists in menu
                    if (menuItems.containsKey(itemId)) {
                        order.addItem(menuItems.get(itemId));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading orders: " + e.getMessage());
        }

        return orders;
    }

    // Find an order by ID
    private static Order findOrderById(List<Order> orders, int orderId) {
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public static void saveOrders(List<Order> orders) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "orders.csv"))) {
            for (Order order : orders) {
                for (MenuItem item : order.getItems()) {
                    // Format: orderId,customerId,timestamp,itemId,isOnline
                    writer.println(order.getOrderId() + "," +
                            order.getCustomerId() + "," +
                            order.getFormattedTimestamp() + "," +
                            item.getId() + "," +
                            order.isOnline());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }

    public static void logEvent(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            LocalDateTime now = LocalDateTime.now();
            writer.write(now.format(DATE_FORMATTER) + " - " + message + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
            // Try to create the file if it doesn't exist
            try {
                new File(LOG_FILE).createNewFile();
                logEvent(message); // Retry
            } catch (IOException ex) {
                System.err.println("Failed to create log file: " + ex.getMessage());
            }
        }
    }

    public static void saveReport(String report) {
        try (FileWriter writer = new FileWriter(REPORT_FILE)) {
            writer.write(report);
        } catch (IOException e) {
            System.err.println("Error saving report: " + e.getMessage());
            // Try to create the file if it doesn't exist
            try {
                new File(REPORT_FILE).createNewFile();
                saveReport(report); // Retry
            } catch (IOException ex) {
                System.err.println("Failed to create report file: " + ex.getMessage());
            }
        }
    }
}
package coffeeshop.simulation;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.util.FileManager;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DiscountManager {

    public void applyDiscount(Order order) {
        double discountPercentage = 0;
        String discountReason = "";

        // Rule 1: Happy Hour (8-10 AM) - 15% discount
        LocalTime orderTime = order.getTimestamp().toLocalTime();
        LocalTime happyHourStart = LocalTime.of(8, 0);
        LocalTime happyHourEnd = LocalTime.of(10, 0);

        if (orderTime.isAfter(happyHourStart) && orderTime.isBefore(happyHourEnd)) {
            discountPercentage = 15;
            discountReason = "Happy Hour";
        }

        // Rule 2: Buy 3+ items, get 10% off
        List<MenuItem> items = order.getItems();
        if (items.size() >= 3) {
            discountPercentage = Math.max(discountPercentage, 10);
            discountReason = items.size() + " items bundle";
        }


        boolean hasCoffee = false;
        boolean hasFood = false;

        for (MenuItem item : items) {
            if ("Coffee".equalsIgnoreCase(item.getCategory())) {
                hasCoffee = true;
            } else if ("Food".equalsIgnoreCase(item.getCategory())) {
                hasFood = true;
            }
        }

        if (hasCoffee && hasFood) {
            discountPercentage = Math.max(discountPercentage, 20);
            discountReason = "Coffee + Food combo";
        }


        if (discountPercentage > 0) {
            order.applyDiscount(discountPercentage);
            FileManager.logEvent("Applied " + discountPercentage + "% discount to order #"
                    + order.getOrderId() + " (" + discountReason + ")");
        }
    }
}
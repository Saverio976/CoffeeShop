package coffeeshop.simulation;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.util.FileManager;

import java.util.concurrent.TimeUnit;

public class StaffMember implements Runnable {
    private String name;
    private boolean isWorking;
    private Order currentOrder;
    private CoffeeShopSimulation simulation;
    private static volatile double speedMultiplier = 1.0;

    public StaffMember(String name, CoffeeShopSimulation simulation) {
        this.name = name;
        this.isWorking = true;
        this.simulation = simulation;
    }

    public StaffMember(String name) {
        this.name = name;
        this.isWorking = true;
        this.simulation = null;
    }

    public static void setSpeedMultiplier(double multiplier) {
        speedMultiplier = multiplier;
    }


    @Override
    public void run() {
        FileManager.logEvent("Staff member " + name + " started working");

        while (isWorking) {
            if (simulation == null) {
                FileManager.logEvent("Staff member " + name + " is not assigned to a simulation");
                break;
            }
            // Try to get an order from the queue
            currentOrder = simulation.getNextOrder();

            if (currentOrder != null) {
                processOrder();
            } else {
                // No orders left and simulation is ending
                if (!simulation.isRunning()) {
                    break;
                }

                // Wait a bit before checking again
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        FileManager.logEvent("Staff member " + name + " finished working");
    }

    public void setOrder(Order order) {
        this.currentOrder = order;
    }

    public void processOrder() {
        FileManager.logEvent(name + " is processing order #" + currentOrder.getOrderId() +
                " for customer " + currentOrder.getCustomerId());

        if (simulation != null)
            simulation.updateStaffStatus(this, "Processing order #" + currentOrder.getOrderId());

        int processingTime = 0;

        for (MenuItem item : currentOrder.getItems()) {
            processingTime += item.getTimeTaken();
        }
        processingTime /= speedMultiplier;

        try {
            TimeUnit.MILLISECONDS.sleep(processingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mark as processed
        currentOrder.setProcessed(true);
        if (simulation != null) {
            simulation.orderCompleted(currentOrder);
            FileManager.logEvent(name + " completed order #" + currentOrder.getOrderId());
            simulation.updateStaffStatus(this, "Available");
        }

        currentOrder = null;
    }

    public void stopWorking() {
        this.isWorking = false;
    }

    // Getters
    public String getName() { return name; }
    public Order getCurrentOrder() { return currentOrder; }
    public String getStatus() {
        return (currentOrder == null) ? "Available" :
                "Processing order #" + currentOrder.getOrderId();
    }
}
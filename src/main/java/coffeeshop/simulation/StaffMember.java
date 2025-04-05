package coffeeshop.simulation;

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

    public static void setSpeedMultiplier(double multiplier) {
        speedMultiplier = multiplier;
    }


    @Override
    public void run() {
        FileManager.logEvent("Staff member " + name + " started working");

        while (isWorking) {
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

    private void processOrder() {
        FileManager.logEvent(name + " is processing order #" + currentOrder.getOrderId() +
                " for customer " + currentOrder.getCustomerId());

        simulation.updateStaffStatus(this, "Processing order #" + currentOrder.getOrderId());

        // Simulate time to process the order - more items take longer
        int processingTime = (int)((2000 + (currentOrder.getItems().size() * 1000)) / speedMultiplier);


        try {
            TimeUnit.MILLISECONDS.sleep(processingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mark as processed
        currentOrder.setProcessed(true);
        simulation.orderCompleted(currentOrder);

        FileManager.logEvent(name + " completed order #" + currentOrder.getOrderId());
        simulation.updateStaffStatus(this, "Available");

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
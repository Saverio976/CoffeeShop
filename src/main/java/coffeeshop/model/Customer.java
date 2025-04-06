package coffeeshop.model;

import coffeeshop.exceptions.EmptyCustomerID;

public class Customer {
    private String id;
    private Order currentOrder;

    public Customer(String id) throws EmptyCustomerID {
        if (id == null || id.isEmpty())
            throw new EmptyCustomerID("Customer ID cannot be null or empty");
        this.id = id;
    }

    public void createOrder() {
        this.currentOrder = new Order(id);
    }

    // Getters and setters
    public String getId() { return id; }
    public Order getCurrentOrder() { return currentOrder; }
    public void setCurrentOrder(Order order) { this.currentOrder = order; }
}
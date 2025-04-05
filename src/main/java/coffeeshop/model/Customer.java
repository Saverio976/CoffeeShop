package coffeeshop.model;

public class Customer {
    private String id;
    private Order currentOrder;

    public Customer(String id) {
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
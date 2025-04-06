package coffeeshop.model;

import coffeeshop.exceptions.InvalidPriceException;

public class MenuItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int timeTaken;

    public MenuItem(String id, String name, String description, double price, String category) throws InvalidPriceException {
        this.id = id;
        this.name = name;
        this.description = description;
        if (price < 0)
            throw new InvalidPriceException("Price cannot be negative");
        this.price = price;
        this.category = category;
        this.timeTaken = 1000; // default
    }

    public MenuItem(String id, String name, String description, double price, int timeTaken, String category) throws InvalidPriceException {
        this.id = id;
        this.name = name;
        this.description = description;
        if (price < 0)
            throw new InvalidPriceException("Price cannot be negative");
        this.price = price;
        this.category = category;
        this.timeTaken = timeTaken;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public int getTimeTaken() { return timeTaken; }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price);
    }
}
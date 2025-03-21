package com.hwjustjava.app;
import java.util.HashMap;
import java.util.Map;

class OrderManager
{
    private java.util.Queue<Order> Orders;
    private java.util.List<Order> CompletedOrders;
    private int NumberWaitingOrders;

    public OrderManager()
    {
        Orders = new java.util.LinkedList<Order>();
        CompletedOrders = new java.util.LinkedList<Order>();
        NumberWaitingOrders = 0;
    }

    public OrderManager(String CsvFilePath) throws InvalidOrderCsvException
    {
        Orders = new java.util.LinkedList<Order>();
        CompletedOrders = new java.util.LinkedList<Order>();
        NumberWaitingOrders = 0;
        try {
            FromCsvFile(CsvFilePath);
        } catch (InvalidOrderCsvException e) {
            System.out.println("Error loading orders: " + e.getMessage());
            throw e;
        }
    }

    public void FromCsvFile(String CsvFilePath) throws InvalidOrderCsvException
    {
        java.io.BufferedReader buff = null;
        String row[] = new String[3];
        java.util.LinkedList<String> itemsID = new java.util.LinkedList<String>();
        java.time.Instant lastInstant = null;
        String lastCustomerID = "";

        try {
            buff = new java.io.BufferedReader(new java.io.FileReader(CsvFilePath));
            String inputLine = buff.readLine();
            while (inputLine != null)
            {
                row = inputLine.split(",");
                if (row.length == 0) {
                    inputLine = buff.readLine();
                    continue;
                }
                if (row.length != 3) {
                    throw new InvalidOrderCsvException("Invalid number of rows");
                }
                int epochSecond = 0;
                try {
                    epochSecond = Integer.parseInt(row[0].trim());
                } catch (NumberFormatException e) {
                    inputLine = buff.readLine();
                    continue;
                }
                java.time.Instant time = java.time.Instant.ofEpochSecond(epochSecond);
                if (lastInstant != null && itemsID.size() != 0 && (!lastCustomerID.equals(row[1].trim()) || !lastInstant.equals(time))) {
                    CreateOrders(itemsID, lastCustomerID, lastInstant);
                    itemsID.clear();
                }
                lastCustomerID = row[1].trim();
                lastInstant = time;
                itemsID.add(row[2].trim());
                inputLine = buff.readLine();
            }
            if (lastInstant != null && itemsID.size() != 0) {
                CreateOrders(itemsID, lastCustomerID, lastInstant);
            }
        } catch (java.io.FileNotFoundException e) {
            throw new InvalidOrderCsvException("File not found: " + CsvFilePath);
        } catch (java.io.IOException e) {
            throw new InvalidOrderCsvException("Error reading file");
        } catch (NumberFormatException e) {
            throw new InvalidOrderCsvException("Number format Exception");
        } catch (InvalidOrderException e) {
            throw new InvalidOrderCsvException("Invalid order: " + e.getMessage());
        }
    }

    public Invoice CreateOrders(java.util.List<String> ItemsID, String CustomerID, java.time.Instant Time) throws InvalidOrderException
    {
        java.util.List<Order> orders = new java.util.LinkedList<Order>();
        float discount = 0;

        try {
            discount = CalculateDiscount(ItemsID);
        } catch (InvalidOrderException e) {
            throw e;
        }

        for (String itemID : ItemsID)
        {
            try {
                Order order = new Order(itemID, CustomerID, Time, discount);
                orders.add(order);
                Orders.add(order);
                NumberWaitingOrders = NumberWaitingOrders + 1;
            } catch (UnknownItemException e) {
                System.out.println(itemID);
                throw new InvalidOrderException("Unknown item: " + itemID);
            }
        }
        Invoice invoice = CoffeeManager.GetInstance().GetInvoiceManager().ProcessOrders(orders);
        return invoice;
    }

    public float CalculateDiscount(java.util.List<String> ItemsID) throws InvalidOrderException
    {
        float discount = 0;
        Map<String, Integer> Categories = new HashMap<>();
        Categories.put("Merchandise", 0);
        Categories.put("Food", 0);
        Categories.put("Beverage.HotBeverage", 0);
        Categories.put("Beverage.ColdBeverage", 0);

        for (String itemID : ItemsID)
        {
            try {
                IItem item = CoffeeManager.GetInstance().GetMenu().GetItem(itemID);
                String category = item.GetCategory();
                Categories.put(category, Categories.get(category) + 1);
            } catch (UnknownItemException e) {
                throw new InvalidOrderException("Unknown item: " + itemID);
            }
        }

        if (Categories.get("Merchandise") >= 3 || Categories.get("Food") >= 3 || Categories.get("Beverage.HotBeverage") >= 3 || Categories.get("Beverage.ColdBeverage") >= 3)
            discount += 0.1f;

        if (Categories.get("Merchandise") > 0 && Categories.get("Food") > 0 && Categories.get("Beverage.HotBeverage") > 0 && Categories.get("Beverage.ColdBeverage") > 0)
            discount += 0.15f;

        return discount;
    }

    public java.util.List<Order> GetCompletedOrders()
    {
        return CompletedOrders;
    }

    public int GetNumberWaitingOrders()
    {
        return NumberWaitingOrders;
    }
}

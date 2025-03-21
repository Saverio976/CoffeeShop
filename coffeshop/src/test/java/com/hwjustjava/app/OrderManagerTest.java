package com.hwjustjava.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OrderManagerTest {

    @Test
    public void CreateOrdersOK() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "0", "Test 0.", (float) 3.99);

            OrderManager ordManager = new OrderManager();
            java.util.List<String> itemsID = new java.util.LinkedList<String>();

            itemsID.add("Food.0");
            Invoice invoice = ordManager.CreateOrders(itemsID, "Tester McTesting", java.time.Instant.now());

            if (invoice.GetOrders().size() != 1)
                assertTrue(false);
            if (invoice.GetTotalCost() < 3.9)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidItemException e) {
            assertTrue(false);
        } catch (InvalidOrderException e) {
            assertTrue(false);
        }
    }

    @Test
    public void CreateOrdersUnknownItem() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "0", "Test 0.", (float) 3.99);

            OrderManager ordManager = new OrderManager();
            java.util.List<String> itemsID = new java.util.LinkedList<String>();

            itemsID.add("Food.Unknown");
            Invoice invoice = ordManager.CreateOrders(itemsID, "Tester McTesting", java.time.Instant.now());
            assertTrue(false);
        } catch (InvalidItemException e) {
            assertTrue(false);
        } catch (InvalidOrderException e) {
            assertTrue(true);
        }
    }

    @Test
    public void CreateOrdersMultipleItems() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "0", "Test 0.", (float) 3.99);
            CoffeeManager.GetInstance().GetMenu().CreateItem("Merchandise", "1", "Test 1.", (float) 3.99);

            OrderManager ordManager = new OrderManager();
            java.util.List<String> itemsID = new java.util.LinkedList<String>();

            itemsID.add("Food.0");
            itemsID.add("Merchandise.1");
            Invoice invoice = ordManager.CreateOrders(itemsID, "Tester McTesting", java.time.Instant.now());
            if (invoice.GetOrders().size() != 2)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidItemException e) {
            assertTrue(false);
        } catch (InvalidOrderException e) {
            assertTrue(false);
        }
    }

    @Test
    public void CreateMultipleOrdersOneClient() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "0", "Test 0.", (float) 3.99);
            CoffeeManager.GetInstance().GetMenu().CreateItem("Merchandise", "1", "Test 1.", (float) 3.99);

            OrderManager ordManager = new OrderManager();
            java.util.List<String> itemsID = new java.util.LinkedList<String>();

            itemsID.add("Food.0");
            Invoice invoice = ordManager.CreateOrders(itemsID, "Tester McTesting", java.time.Instant.now());
            itemsID.clear();
            itemsID.add("Food.0");
            itemsID.add("Merchandise.1");
            Invoice invoice2 = ordManager.CreateOrders(itemsID, "Tester McTesting", java.time.Instant.now());
            if (invoice.GetOrders().size() != 1 || invoice2.GetOrders().size() != 2)
                assertTrue(false);
            if (CoffeeManager.GetInstance().GetInvoiceManager().GetTotalIncome() < 11.9)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidItemException e) {
            assertTrue(false);
        } catch (InvalidOrderException e) {
            assertTrue(false);
        }
    }

    @Test
    public void CreateMultipleOrdersMultipleClients() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "0", "Test 0.", (float) 3.99);
            CoffeeManager.GetInstance().GetMenu().CreateItem("Merchandise", "1", "Test 1.", (float) 3.99);

            OrderManager ordManager = new OrderManager();
            java.util.List<String> itemsID = new java.util.LinkedList<String>();

            itemsID.add("Food.0");
            Invoice invoice = ordManager.CreateOrders(itemsID, "Tester McTesting", java.time.Instant.now());
            itemsID.clear();
            itemsID.add("Food.0");
            itemsID.add("Merchandise.1");
            Invoice invoice2 = ordManager.CreateOrders(itemsID, "Another McTesting", java.time.Instant.now());
            if (invoice.GetOrders().size() != 1 || invoice2.GetOrders().size() != 2)
                assertTrue(false);
            if (CoffeeManager.GetInstance().GetInvoiceManager().GetTotalIncome() < 11.9)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidItemException e) {
            assertTrue(false);
        } catch (InvalidOrderException e) {
            assertTrue(false);
        }
    }

    @Test
    public void CreateMultipleOrdersDiscountSameType() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "0", "Test 0.", (float) 3.99);
            CoffeeManager.GetInstance().GetMenu().CreateItem("Merchandise", "1", "Test 1.", (float) 3.99);

            OrderManager ordManager = new OrderManager();
            java.util.List<String> itemsID = new java.util.LinkedList<String>();

            itemsID.add("Food.0");
            Invoice invoice = ordManager.CreateOrders(itemsID, "Tester McTesting", java.time.Instant.now());
            itemsID.clear();
            itemsID.add("Food.0");
            itemsID.add("Food.0");
            itemsID.add("Food.0");
            Invoice invoice2 = ordManager.CreateOrders(itemsID, "Another McTesting", java.time.Instant.now());
            if (invoice.GetOrders().size() != 1 || invoice2.GetOrders().size() != 3)
                assertTrue(false);
            if (CoffeeManager.GetInstance().GetInvoiceManager().GetTotalIncome() < 14.5 || CoffeeManager.GetInstance().GetInvoiceManager().GetTotalIncome() > 15.0)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidItemException e) {
            assertTrue(false);
        } catch (InvalidOrderException e) {
            assertTrue(false);
        }
    }

    @Test
    public void CreateMultipleOrdersDiscountVariety() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "0", "Test 0.", (float) 3.99);
            CoffeeManager.GetInstance().GetMenu().CreateItem("Merchandise", "1", "Test 1.", (float) 3.99);
            CoffeeManager.GetInstance().GetMenu().CreateItem("HotBeverage", "2", "Test 2.", (float) 3.99);
            CoffeeManager.GetInstance().GetMenu().CreateItem("ColdBeverage", "3", "Test 3.", (float) 3.99);

            OrderManager ordManager = new OrderManager();
            java.util.List<String> itemsID = new java.util.LinkedList<String>();

            itemsID.add("Food.0");
            Invoice invoice = ordManager.CreateOrders(itemsID, "Tester McTesting", java.time.Instant.now());
            itemsID.clear();
            itemsID.add("Food.0");
            itemsID.add("Merchandise.1");
            itemsID.add("Beverage.HotBeverage.2");
            itemsID.add("Beverage.ColdBeverage.3");
            Invoice invoice2 = ordManager.CreateOrders(itemsID, "Another McTesting", java.time.Instant.now());
            if (invoice.GetOrders().size() != 1 || invoice2.GetOrders().size() != 4)
                assertTrue(false);
            if (CoffeeManager.GetInstance().GetInvoiceManager().GetTotalIncome() < 17.0 || CoffeeManager.GetInstance().GetInvoiceManager().GetTotalIncome() > 18.0)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidItemException e) {
            assertTrue(false);
        } catch (InvalidOrderException e) {
            assertTrue(false);
        }
    }
}

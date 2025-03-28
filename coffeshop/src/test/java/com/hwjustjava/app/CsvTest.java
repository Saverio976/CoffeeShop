package com.hwjustjava.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CsvTest {

    /*@Test
    public void LoadCSVMenuOK() {
        try {
            Menu menu = new Menu("src/test/files/menuOK.csv");
            if (menu.GetItems().size() != 1)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidMenuCsvException e) {
            assertTrue(false);
        }
    }

    @Test
    public void LoadCSVMenuKO() {
        try {
            Menu menu = new Menu("src/test/files/menuKO.csv");
            assertTrue(false);
        } catch (InvalidMenuCsvException e) {
            assertTrue(true);
        }
    }

    @Test
    public void LoadCSVOrdersOK() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().FromCsvFile("src/test/files/menuOK.csv");
            OrderManager ordManager = new OrderManager();
            ordManager.FromCsvFile("src/test/files/ordersOK.csv");
            if (ordManager.GetNumberWaitingOrders() != 1)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidOrderCsvException e) {
            assertTrue(false);
        } catch (InvalidMenuCsvException e) {
            assertTrue(false);
        }
    }

    @Test
    public void LoadCSVOrdersKO() {
        try {
            OrderManager ordManager = new OrderManager();
            ordManager.FromCsvFile("src/test/files/ordersKO.csv");
            assertTrue(false);
        } catch (InvalidOrderCsvException e) {
            assertTrue(true);
        }
    }

    @Test
    public void LoadCSVMenuEmpty() {
        try {
            Menu menu = new Menu("src/test/files/emptyFile.csv");
            assertTrue(true);
        } catch (InvalidMenuCsvException e) {
            assertTrue(false);
        }
    }

    @Test
    public void LoadCSVOrdersEmpty() {
        try {
            OrderManager ordManager = new OrderManager();
            ordManager.FromCsvFile("src/test/files/emptyFile.csv");
            assertTrue(true);
        } catch (InvalidOrderCsvException e) {
            assertTrue(false);
        }
    }

    @Test
    public void LoadCSVOrderMultiple() {
        try {
            CoffeeManager.GetInstance().Reset();
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "0", "Test 0.", (float)4);
            CoffeeManager.GetInstance().GetMenu().CreateItem("Food", "1", "Test 1.", (float)4);
            CoffeeManager.GetInstance().GetOrderManager().FromCsvFile("src/test/files/ordersMultiple.csv");
            int size = CoffeeManager.GetInstance().GetInvoiceManager().GetInvoices().size();
            System.out.println("size: " + size);
            assertTrue(size == 3, "only 3 invoice must be");
        } catch (InvalidOrderCsvException e) {
            assertTrue(false, "error: " + e.getMessage());
        } catch (InvalidItemException e) {
            assertTrue(false, "error: " + e.getMessage());
        }
    }*/
}

package com.hwjustjava.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MenuTest {

    @Test
    public void CreateOrderable() {
        try {
            Menu menu = new Menu();
            menu.CreateItem("Food", "0", "Test 0.", (float)3.99);

            int n = menu.GetItems().size();
            if (n != 1) {
                assertTrue(false);
            }
            assertTrue(true);
        } catch (InvalidItemException e) {
            assertTrue(false);
        }
    }

    @Test
    public void CreateInvalidOrderable() {
        try {
            Menu menu = new Menu();
            menu.CreateItem("Error", "1", "Test 1.", (float)3.99);
            assertTrue(false);
        } catch (InvalidItemException e) {
            assertTrue(true);
        }

        try {
            Menu menu = new Menu();
            menu.CreateItem("Food", "2", "Test 2.", -(float)3.99);
            assertTrue(false);
        } catch (InvalidItemException e) {
            assertTrue(true);
        }

        try {
            Menu menu = new Menu();
            menu.CreateItem("Food", "3", "Test 3.", (float)3.99);
            menu.CreateItem("Food", "3", "Test 3.", (float)3.99);
            assertTrue(false);
        } catch (InvalidItemException e) {
            assertTrue(true);
        }
    }

    @Test
    public void CreateMultipleOrderables() {
        try {
            Menu menu = new Menu();
            menu.CreateItem("Food", "4", "Test 4.", (float)3.99);
            menu.CreateItem("Merchandise", "5", "Test 5.", (float)3.99);
            menu.CreateItem("HotBeverage", "6", "Test 6.", (float)3.99);

            int n = menu.GetItems().size();
            if (n != 3)
                assertTrue(false);
            assertTrue(true);
        } catch (InvalidItemException e) {
            assertTrue(false);
        }
    }
}

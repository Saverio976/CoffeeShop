package com.hwjustjava.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomerManagerTest {

    /*@Test
    public void CreateCustomerOK() {
        CustomerManager custManager = new CustomerManager();
        try {
            Customer cust = custManager.GetOrCreate("Tester McTesting");
            int count = custManager.GetCustomersCount();
            assertTrue(count == 1);
        } catch (InvalidCustomerException e) {
            assertTrue(false);
        }
    }

    @Test
    public void CreateCustomerKO() {
        CustomerManager custManager = new CustomerManager();
        try {
            Customer cust = custManager.GetOrCreate("");
            assertTrue(false);
        } catch (InvalidCustomerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void CreateMultipleCustomers() {
        CustomerManager custManager = new CustomerManager();
        try {
            Customer cust = custManager.GetOrCreate("Frodo Baggins");
            cust = custManager.GetOrCreate("Sam Gamgee");
            int count = custManager.GetCustomersCount();
            assertTrue(count == 2);
        } catch (InvalidCustomerException e) {
            assertTrue(false);
        }
    }*/
}

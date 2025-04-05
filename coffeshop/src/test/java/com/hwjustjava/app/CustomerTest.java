package com.hwjustjava.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    public void CreateCustomerOK() {
        try {
            Customer customer = new Customer("Frodo", Collections.emptyList());
            assertTrue(true);
        } catch (InvalidCustomerException e) {
            assertTrue(false);
        }
    }
}

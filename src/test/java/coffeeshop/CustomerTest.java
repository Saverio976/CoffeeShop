package coffeeshop;

import coffeeshop.model.Customer;
import coffeeshop.model.Order;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerTest {
    @Test
    void createSimpleCustomer()
    {
        try {
            Customer customer = new Customer("Frodo");

            if (!Objects.equals(customer.getId(), "Frodo"))
                assertTrue(false);
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }

    @Test
    void setOrderToCustomer()
    {
        try {
            Customer customer = new Customer("Frodo");

            customer.createOrder();
            if (customer.getCurrentOrder() == null)
                assertTrue(false);
            if (!Objects.equals(customer.getCurrentOrder().getCustomerId(), customer.getId()))
                assertTrue(false);
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }

    @Test
    void invalidCustomer()
    {
        try {
            Customer customer = new Customer("");

            if (customer.getId() != "")
                assertTrue(false);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}
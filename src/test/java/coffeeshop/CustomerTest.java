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
        Customer customer = new Customer("Frodo");

        if (!Objects.equals(customer.getId(), "Frodo"))
            assertTrue(false);
        assertTrue(true);
    }

    @Test
    void setOrderToCustomer()
    {
        Customer customer = new Customer("Frodo");

        customer.createOrder();
        if (customer.getCurrentOrder() == null)
            assertTrue(false);
        if (!Objects.equals(customer.getCurrentOrder().getCustomerId(), customer.getId()))
            assertTrue(false);
        assertTrue(true);
    }
}
package coffeeshop;

import coffeeshop.model.Customer;
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
}
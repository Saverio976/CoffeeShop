package coffeeshop;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.model.MenuItem;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {
    @Test
    void createTwoSimpleOrders()
    {
        Order order = new Order("Frodo");
        if (!Objects.equals(order.getCustomerId(), "Frodo"))
            assertTrue(false);

        Order order2 = new Order("Sam");
        if (!Objects.equals(order2.getCustomerId(), "Sam"))
            assertTrue(false);

        if (order.getOrderId() == order2.getOrderId())
            assertTrue(false);
        if (order.getOrderId() != 1001)
            assertTrue(false);
        if (order2.getOrderId() != 1002)
            assertTrue(false);
        assertTrue(true);
    }
    @Test
    void setItemToOrder()
    {
        Order order = new Order("Frodo");
        MenuItem item = new MenuItem("COFFEE", "Coffee", "Hot beverage.", 2.50, "HOT_BEVERAGE");
        order.addItem(item);

        if (order.getItems().size() != 1)
            assertTrue(false);
        if (order.getItems().get(0).getName() != item.getName())
            assertTrue(false);
        if (order.getTotalAmount() <= 2.40 || order.getTotalAmount() >= 2.60)
            assertTrue(false);
        assertTrue(true);
    }

    @Test
    void applyDiscountToOrder()
    {
        Order order = new Order("Frodo");
        MenuItem item1 = new MenuItem("COFFEE", "Coffee", "Hot beverage.", 2.50, "HOT_BEVERAGE");
        MenuItem item2 = new MenuItem("TEA", "Tea", "Hot beverage.", 1.50, "HOT_BEVERAGE");
        order.addItem(item1);
        order.addItem(item2);

        order.applyDiscount(10); // Apply a 10% discount

        if (order.getTotalAmount() <= 3.50 || order.getTotalAmount() >= 3.70)
            assertTrue(false);
        assertTrue(true);
    }
}
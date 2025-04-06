package coffeeshop;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.simulation.DiscountManager;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscountTest {
    @Test
    void DiscountFor3Item()
    {
        try {
            DiscountManager discountManager = new DiscountManager();
            Order order = new Order("Frodo");
            MenuItem item1 = new MenuItem("COFFEE", "Coffee", "Hot beverage.", 2.50, "COFFEE");
            MenuItem item2 = new MenuItem("TEA", "Tea", "Hot beverage.", 1.50, "COFFEE");
            MenuItem item3 = new MenuItem("MACHIATTO", "Machiatto", "Hot beverage.", 3.00, "COFFEE");

            order.addItem(item1);
            order.addItem(item2);

            discountManager.applyDiscount(order);

            if (order.getTotalAmount() <= 3.90 || order.getTotalAmount() >= 4.10)
                assertTrue(false);

            order.addItem(item3);
            discountManager.applyDiscount(order);

            if (order.getTotalAmount() <= 6.20 || order.getTotalAmount() >= 6.40)
                assertTrue(false);
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }

    @Test
    void DiscountCoffeeFood()
    {
        try {
            DiscountManager discountManager = new DiscountManager();
            Order order = new Order("Frodo");
            MenuItem item1 = new MenuItem("COFFEE", "Coffee", "Hot beverage.", 2.50, "COFFEE");
            MenuItem item2 = new MenuItem("BISCUIT", "Biscuit", "cookie.", 1.50, "FOOD");

            order.addItem(item1);
            order.addItem(item2);

            discountManager.applyDiscount(order);

            if (order.getTotalAmount() <= 3.10 || order.getTotalAmount() >= 3.30)
                assertTrue(false);
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }
}
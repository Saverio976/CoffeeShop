package coffeeshop;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.simulation.StaffMember;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StaffTest {
    @Test
    void SingleStaffTest()
    {
        try {
            Order order = new Order("Frodo");
            MenuItem item = new MenuItem("COFFEE", "Coffee", "Hot beverage.", 2.50, 1000, "HOT_BEVERAGE");
            StaffMember staff = new StaffMember("Sam");
            staff.setSpeedMultiplier(10);

            order.addItem(item);

            if (order.getItems().size() != 1)
                assertTrue(false);

            staff.setOrder(order);

            long startTime = System.nanoTime();
            staff.processOrder();
            long endTime = System.nanoTime();

            long elapsedMillis = (endTime - startTime) / 1_000_000;

            System.out.println("Elapsed time: " + elapsedMillis + " ms");
            if (elapsedMillis > 150 || elapsedMillis < 50)
                assertTrue(false);
        } catch (Exception e) {
            assertTrue(false);
        }

        assertTrue(true);
    }
}
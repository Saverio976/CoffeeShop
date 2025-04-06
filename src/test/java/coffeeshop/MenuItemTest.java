package coffeeshop;

import org.junit.jupiter.api.Test;
import coffeeshop.model.MenuItem;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuItemTest {
    @Test
    void createTwoSimpleMenuItems()
    {
        try {
            MenuItem item1 = new MenuItem("COFFEE", "Coffee", "Hot beverage.", 2.50, "HOT_BEVERAGE");
            MenuItem item2 = new MenuItem("TEA", "Tea", "Hot beverage.", 1.50, "HOT_BEVERAGE");

            if (!item1.getName().equals("Coffee"))
                assertTrue(false);
            if (!item2.getName().equals("Tea"))
                assertTrue(false);

            if (!item1.getDescription().equals("Hot beverage."))
                assertTrue(false);
            if (!item2.getDescription().equals("Hot beverage."))
                assertTrue(false);

            if (item1.getPrice() <= 2.40 || item1.getPrice() >= 2.60)
                assertTrue(false);
            if (item2.getPrice() <= 1.40 || item2.getPrice() >= 1.60)
                assertTrue(false);

            if (!item1.getCategory().equals("HOT_BEVERAGE") || !item2.getCategory().equals("HOT_BEVERAGE"))
                assertTrue(false);

            if (item1.getId() == item2.getId())
                assertTrue(false);
        } catch (Exception e) {
            assertTrue(false);
        }

        assertTrue(true);
    }
}
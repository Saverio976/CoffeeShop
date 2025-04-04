package com.hwjustjava.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StaffTest {

    @Test
    public void CreateSimpleStaff() {
        Staff staff = new Staff("Sam");
        if (staff.GetId() != "Sam")
            assertTrue(false);
        assertTrue(true);
    }

    @Test
    public void AddStaffToStaffManager() {
        StaffManager staffManager = new StaffManager(1);

        if (staffManager.GetNumberOfStaffs() != 0)
            assertTrue(false);

        staffManager.AddStaff("Sam");

        if (staffManager.GetNumberOfStaffs() != 1)
            assertTrue(false);

        assertTrue(true);
    }
}

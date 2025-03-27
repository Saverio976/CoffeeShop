package com.hwjustjava.app;

class StaffManager
{
    Staff[] staffs;

    public StaffManager(int nbStaff)
    {
        this.staffs = new Staff[nbStaff];

        for (int i = 0; i < nbStaff; i++) {
            this.staffs[i] = new Staff("Thread " + i);
        }
    }

    public void Start()
    {
        for (int i = 0; i < staffs.length; i++) {
            this.staffs[i].start();
        }
    }
}

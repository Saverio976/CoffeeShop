package com.hwjustjava.app;

class StaffManager
{
    Staff[] staffs;
    float speed;

    public StaffManager(int nbStaff)
    {
        this.speed = 1.0f;
        this.staffs = new Staff[nbStaff];

        for (int i = 0; i < nbStaff; i++) {
            this.staffs[i] = null;
        }
    }

    public void Start()
    {
        for (int i = 0; i < staffs.length; i++) {
            this.staffs[i].start();
        }
    }

    public float GetSpeed()
    {
        return this.speed;
    }

    public void SetSpeed(float speed)
    {
        this.speed = speed;
    }

    public void AddStaff(String name)
    {
        Staff staff = new Staff(name);

        staff.start();
        for (int i = 0; i < staffs.length; i++) {
            if (staffs[i] == null) {
                staffs[i] = staff;
                break;
            }
        }
    }

    public int GetNumberOfStaffs()
    {
        int c = 0;

        for (int i = 0; i < staffs.length; i++)
            if (staffs[i] != null)
                c += 1;
        return c;
    }
}

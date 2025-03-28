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
            this.staffs[i] = new Staff("Thread " + i);
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
}

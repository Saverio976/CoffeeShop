package com.hwjustjava.app;

class HotBeverage extends IBeverage
{
    private String ID;
    private String Category;
    private String Description;
    private float Cost;
    private float PreparationTime;

    public HotBeverage(String aID, String aDescription, float aCost, float aPreparationTime)
    {
        Category = "Beverage.HotBeverage";
        ID = Category + "." + aID;
        Description = aDescription;
        Cost = aCost;
        PreparationTime = aPreparationTime;
    }

    public String GetID()
    {
        return ID;
    }

    public String GetCategory()
    {
        return Category;
    }

    public String GetDescription()
    {
        return Description;
    }

    public float GetCost()
    {
        return Cost;
    }

    public float GetPreparationTime()
    {
        return PreparationTime;
    }
}

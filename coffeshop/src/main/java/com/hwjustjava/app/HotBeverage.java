package com.hwjustjava.app;

class HotBeverage extends IBeverage
{
    private String ID;
    private String Category;
    private String Description;
    private float Cost;

    public HotBeverage(String aID, String aDescription, float aCost)
    {
        Category = "Beverage.HotBeverage";
        ID = Category + "." + aID;
        Description = aDescription;
        Cost = aCost;
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
}

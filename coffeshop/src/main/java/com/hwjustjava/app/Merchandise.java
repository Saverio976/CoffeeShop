package com.hwjustjava.app;

class Merchandise implements IItem
{
    private String ID;
    private String Category;
    private String Description;
    private float Cost;
    private float PreparationTime;

    public Merchandise(String aID, String aDescription, float aCost, float aPreparationTime)
    {
        Category = "Merchandise";
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

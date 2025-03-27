package com.hwjustjava.app;

class Order
{
    private String ItemID;
    private String CustomerID;
    private java.time.Instant Time;
    private float discount = 0.0f;

    public Order(String aItemID, String aCustomerID, java.time.Instant aTime, float aDiscount) throws UnknownItemException
    {
        CoffeeManager.GetInstance().GetMenu().GetItem(aItemID);
        ItemID = aItemID;
        CustomerID = aCustomerID;
        Time = aTime;
        discount = aDiscount;
    }

    public String GetItemID()
    {
        return ItemID;
    }

    public String GetCustomerID()
    {
        return CustomerID;
    }

    public java.time.Instant GetTime()
    {
        return Time;
    }

    public float GetDiscount() {
        return discount;
    }
}

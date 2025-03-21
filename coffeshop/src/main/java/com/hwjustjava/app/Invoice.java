package com.hwjustjava.app;

class Invoice
{
    private java.util.List<Order> Orders;
    private float TotalCost;

    public Invoice(java.util.List<Order> aOrders)
    {
        Orders = aOrders;
        TotalCost = 0;
        for (Order order : Orders)
        {
            try {
                IItem item = CoffeeManager.GetInstance().GetMenu().GetItem(order.GetItemID());
                TotalCost += item.GetCost() * (1. - order.GetDiscount());
            }
            catch (UnknownItemException e) {
                System.out.println("Unknown item: " + order.GetItemID());
            }
        }
    }

    public java.util.List<Order> GetOrders()
    {
        return Orders;
    }

    public float GetTotalCost()
    {
        return TotalCost;
    }
}

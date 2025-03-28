package com.hwjustjava.app;

class Customer
{
    private String ID;
    private java.util.List<Order> Orders;

    public Customer(String aID, java.util.List<Order> aOrders) throws InvalidCustomerException
    {
        if (aID.isEmpty())
        {
            Log.getInstance().print("Attempt to create customer with empty ID.");
            throw new InvalidCustomerException("Customer ID must not be empty.");
        }
        if (aID.length() > 36) {
            Log.getInstance().print("Attempt to create customer with ID longer than 36 characters: " +aID);
            throw new InvalidCustomerException("Customer ID must be 36 characters or lower.");
        }
        ID = aID;
        Orders = aOrders;
    }

    public String GetID()
    {
        return ID;
    }

    public java.util.List<Order> GetOrders()
    {
        return Orders;
    }
}

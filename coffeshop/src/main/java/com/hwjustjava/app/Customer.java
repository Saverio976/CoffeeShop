package com.hwjustjava.app;

class Customer
{
    private String ID;
    private java.util.List<Order> Orders;

    public Customer(String aID, java.util.List<Order> aOrders) throws InvalidCustomerException
    {
        if (aID.isEmpty())
        {
            throw new InvalidCustomerException("Customer ID must not be empty.");
        }
        if (aID.length() > 36) {
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

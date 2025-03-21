package com.hwjustjava.app;

class CustomerManager
{
    private java.util.HashMap<String, Customer> Customers;

    public CustomerManager()
    {
        Customers = new java.util.HashMap<String, Customer>();
    }

    public Customer GetOrCreate(String CustomerID) throws InvalidCustomerException
    {
        if (Customers.get(CustomerID) == null)
        {
            Customers.put(CustomerID, new Customer(CustomerID));
        }
        return Customers.get(CustomerID);
    }

    public int GetCustomersCount()
    {
        return Customers.size();
    }
}

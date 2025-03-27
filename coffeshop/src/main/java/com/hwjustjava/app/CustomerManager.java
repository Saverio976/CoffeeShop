package com.hwjustjava.app;

class CustomerManager
{
    private java.util.HashMap<String, java.util.List<Customer>> Customers;

    public CustomerManager()
    {
        Customers = new java.util.HashMap<String, java.util.List<Customer>>();
    }

    public String CreateRecordCustomer(String CustomerID) throws InvalidCustomerException
    {
        Customer customer = new Customer(CustomerID, new java.util.LinkedList<Order>());
        if (Customers.get(CustomerID) == null)
        {
            Customers.put(CustomerID, new java.util.LinkedList<Customer>());
        }
        return CustomerID;
    }

    public void AddCustomer(Customer customer)
    {
        if (Customers.get(customer.GetID()) == null)
        {
            Customers.put(customer.GetID(), new java.util.LinkedList<Customer>());
        }
        Customers.get(customer.GetID()).add(customer);
    }

    public int GetCustomersCount()
    {
        return Customers.size();
    }
}

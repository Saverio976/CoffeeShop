package com.hwjustjava.app;

class Customer
{
    private String ID;

    public Customer(String aID) throws InvalidCustomerException
    {
        if (aID.isEmpty())
        {
            throw new InvalidCustomerException("Customer ID must not be empty.");
        }
        if (aID.length() > 36) {
            throw new InvalidCustomerException("Customer ID must be 36 characters or lower.");
        }
        ID = aID;
    }

    public String GetID()
    {
        return ID;
    }
}

package com.hwjustjava.app;

class Statistics
{
    public Statistics()
    {
    }

    public String GetStatistics()
    {
        java.util.List<Customer> customers = CoffeeManager.GetInstance().GetFrontDesk().GetCompletedCustomerOrders();
        java.util.List<Invoice> invoices = CoffeeManager.GetInstance().GetInvoiceManager().GetInvoices();
        String stat = "";

        stat += "Number of customer(s): " + customers.size();
        stat += "\nNumber of Invoice(s): " + invoices.size(); // If a customer has done 2 invoice
        stat += "\nTotal income: " + CoffeeManager.GetInstance().GetInvoiceManager().GetTotalIncome();
        stat += "\nBiggest customer: " + CoffeeManager.GetInstance().GetInvoiceManager().GetBiggestCustomer();
        return stat;
    }

    public void PrintStatistics()
    {
        System.out.println(GetStatistics());
    }
}

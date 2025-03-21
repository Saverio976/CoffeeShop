package com.hwjustjava.app;

class Statistics
{
    public Statistics()
    {
    }

    public void PrintStatistics()
    {
        java.util.List<Order> orders = CoffeeManager.GetInstance().GetOrderManager().GetCompletedOrders();
        java.util.List<Invoice> invoices = CoffeeManager.GetInstance().GetInvoiceManager().GetInvoices();

        System.out.println("Number of Item bought: " + orders.size());
        System.out.println("Number of Invoice: " + invoices.size());
        System.out.println("Total income: " + CoffeeManager.GetInstance().GetInvoiceManager().GetTotalIncome());
        System.out.println("Biggest customer: " + CoffeeManager.GetInstance().GetInvoiceManager().GetBiggestCustomer());
    }
}

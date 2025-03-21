package com.hwjustjava.app;

class InvoiceManager
{
    private java.util.List<Invoice> Invoices;

    public InvoiceManager()
    {
        Invoices = new java.util.LinkedList<Invoice>();
    }

    public Invoice ProcessOrders(java.util.List<Order> Orders)
    {
        Invoice invoice = new Invoice(Orders);
        Invoices.add(invoice);
        return invoice;
    }

    public java.util.List<Invoice> GetInvoices()
    {
        return Invoices;
    }

    public float GetTotalIncome()
    {
        float total = 0.0f;
        for (Invoice i : Invoices)
            total += i.GetTotalCost();
        return total;
    }

    public String GetBiggestCustomer()
    {
        java.util.Map<String, Float> customerTotal = new java.util.HashMap<String, Float>();
        for (Invoice i : Invoices)
            for (Order o : i.GetOrders()) {
                String customer = o.GetCustomerID();
                float total = i.GetTotalCost();
                if (customerTotal.containsKey(customer))
                    total += customerTotal.get(customer);
                customerTotal.put(customer, total);
            }
        String biggestCustomer = "";
        float biggestTotal = 0.0f;
        for (java.util.Map.Entry<String, Float> entry : customerTotal.entrySet())
            if (entry.getValue() > biggestTotal) {
                biggestTotal = entry.getValue();
                biggestCustomer = entry.getKey();
            }
        return biggestCustomer;
    }
}

package com.hwjustjava.app;

class CoffeeManager
{
    private static CoffeeManager SingleInstance = null;

    private OrderManager Orders;
    private CustomerManager CustomersManager;
    private Menu Menus;
    private InvoiceManager Invoices;

    private CoffeeManager()
    {
        Orders = new OrderManager();
        CustomersManager = new CustomerManager();
        Menus = new Menu();
        Invoices = new InvoiceManager();
    }

    public void Reset()
    {
        Orders = new OrderManager();
        CustomersManager = new CustomerManager();
        Menus = new Menu();
        Invoices = new InvoiceManager();
    }

    public static synchronized CoffeeManager GetInstance()
    {
        if (SingleInstance == null)
        {
            SingleInstance = new CoffeeManager();
        }
        return SingleInstance;
    }

    public OrderManager GetOrderManager()
    {
        return Orders;
    }

    public CustomerManager GetCustomerManager()
    {
        return CustomersManager;
    }

    public Menu GetMenu()
    {
        return Menus;
    }

    public InvoiceManager GetInvoiceManager()
    {
        return Invoices;
    }
}

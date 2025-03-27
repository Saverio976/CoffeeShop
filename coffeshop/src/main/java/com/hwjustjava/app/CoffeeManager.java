package com.hwjustjava.app;

class CoffeeManager
{
    private static CoffeeManager SingleInstance = null;

    private FrontDesk TheFrontDesk;
    private CustomerManager CustomersManager;
    private Menu Menus;
    private InvoiceManager Invoices;

    private CoffeeManager()
    {
        TheFrontDesk = new FrontDesk();
        CustomersManager = new CustomerManager();
        Menus = new Menu();
        Invoices = new InvoiceManager();
    }

    public void Reset()
    {
        TheFrontDesk = new FrontDesk();
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

    public FrontDesk GetFrontDesk()
    {
        return TheFrontDesk;
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

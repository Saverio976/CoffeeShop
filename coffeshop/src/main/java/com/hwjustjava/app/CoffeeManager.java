package com.hwjustjava.app;

class CoffeeManager
{
    private static CoffeeManager SingleInstance = null;

    private FrontDesk TheFrontDesk;
    private CustomerManager CustomersManager;
    private Menu Menus;
    private InvoiceManager Invoices;
    private StaffManager TheStaffManager;

    private CoffeeManager()
    {
        TheFrontDesk = new FrontDesk();
        CustomersManager = new CustomerManager();
        Menus = new Menu();
        Invoices = new InvoiceManager();
        TheStaffManager = new StaffManager(2);
    }

    public synchronized void Reset()
    {
        TheFrontDesk = new FrontDesk();
        CustomersManager = new CustomerManager();
        Menus = new Menu();
        Invoices = new InvoiceManager();
        TheStaffManager = new StaffManager(2);
    }

    public static synchronized CoffeeManager GetInstance()
    {
        if (SingleInstance == null)
        {
            SingleInstance = new CoffeeManager();
        }
        return SingleInstance;
    }

    public StaffManager GetStaffManager()
    {
        return TheStaffManager;
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

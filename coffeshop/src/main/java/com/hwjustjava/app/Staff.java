package com.hwjustjava.app;

class Staff extends Thread
{
    String id;
    Customer currentCustomer = null;
    Order currentOrder = null;
    Invoice currentInvoice = null;

    public Staff(String aId)
    {
        id = aId;
    }

    @Override
    public void run()
    {
        synchronized (this.currentCustomer) {
            this.currentCustomer = CoffeeManager.GetInstance().GetFrontDesk().GetWaitingCustomer();
        }
        while (this.currentCustomer != null) {
            java.util.List<Order> orders = this.currentCustomer.GetOrders();
            synchronized (this.currentInvoice) {
                this.currentInvoice = CoffeeManager.GetInstance().GetInvoiceManager().ProcessOrders(this.currentCustomer.GetOrders());
            }
            for (Order order : orders) {
                synchronized (this.currentOrder) {
                    this.currentOrder = order;
                }
                try {
                    // TODO: this should get directly the item from order and order should create the item directly
                    IItem item = CoffeeManager.GetInstance().GetMenu().GetItem(this.currentOrder.GetItemID());
                    Thread.sleep(java.lang.Math.round(item.GetPreparationTime() * 1000));
                } catch (UnknownItemException e) {
                    System.out.println("Unknow item " + e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("Interrupted preparation " + e.getMessage());
                }
            }
            synchronized (this.currentCustomer) {
                this.currentCustomer = CoffeeManager.GetInstance().GetFrontDesk().GetWaitingCustomer();
            }
        }
    }

    public Customer GetCurrentCustomer()
    {
        return this.currentCustomer;
    }

    public Order GetCurrentOrder()
    {
        return this.currentOrder;
    }

    public Invoice GetCurrentInvoice()
    {
        return this.currentInvoice;
    }
}

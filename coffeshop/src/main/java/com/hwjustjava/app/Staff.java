package com.hwjustjava.app;

class Staff extends Thread
{
    String id;
    Customer currentCustomer = null;
    Order currentOrder = null;

    public Staff(String aId)
    {
        id = aId;
    }

    @Override
    public void run()
    {
        synchronized (this.currentCustomer) {
            this.currentCustomer = null;// TODO: get customer from front desk
        }
        while (this.currentCustomer != null) {
            Order[] orders = new Order[10];// TODO: get orders from customer
            for (Order order : orders) {
                synchronized (this.currentOrder) {
                    this.currentOrder = order;
                }
                // TODO: wait for nb seconds;
            }
            // TODO: create invoice for orders with InvoiceManager
            synchronized (this.currentCustomer) {
                this.currentCustomer = null; // TODO: get customer for front desk
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
}

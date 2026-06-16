package com.example.laundryapp;

public class Order {
    private int id;
    private String customerName;
    private String serviceType;
    private double amountCharged;
    private String orderStatus;

    public Order(int id, String customerName, String serviceType, double amountCharged, String orderStatus) {
        this.id = id;
        this.customerName = customerName;
        this.serviceType = serviceType;
        this.amountCharged = amountCharged;
        this.orderStatus = orderStatus;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getServiceType() { return serviceType; }
    public double getAmountCharged() { return amountCharged; }
    public String getOrderStatus() { return orderStatus; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setAmountCharged(double amountCharged) { this.amountCharged = amountCharged; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}

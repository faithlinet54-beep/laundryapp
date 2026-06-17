package com.example.laundryapp;

public class Order {
    private int id;
    private String customerName;
    private String phoneNumber;
    private String serviceType;
    private int quantity;
    private double totalAmount;
    private String orderStatus;

    public Order(int id, String customerName, String phoneNumber, String serviceType, int quantity, double totalAmount, String orderStatus) {
        this.id = id;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.serviceType = serviceType;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getServiceType() { return serviceType; }
    public int getQuantity() { return quantity; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderStatus() { return orderStatus; }

    public void setId(int id) { this.id = id; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}

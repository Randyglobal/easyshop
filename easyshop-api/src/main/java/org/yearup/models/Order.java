package org.yearup.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private LocalDate orderDate;
    private String address;
    private String city;
    private String  state;
    private String zip;
    private BigDecimal shopping_amount;

    private List<OrderLineItem> Items;

    public Order() {
    }

    public Order(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Order(int orderId, int userId, LocalDate orderDate, String address, String city, String state, String zip, BigDecimal shopping_amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.shopping_amount = shopping_amount;
    }

    public List<OrderLineItem> getItems() {
        return Items;
    }

    public void setItems(List<OrderLineItem> items) {
        Items = items;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public BigDecimal getShopping_amount() {
        return shopping_amount;
    }

    public void setShopping_amount(BigDecimal shopping_amount) {
        this.shopping_amount = shopping_amount;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", shopping_amount=" + shopping_amount +
                '}';
    }
}

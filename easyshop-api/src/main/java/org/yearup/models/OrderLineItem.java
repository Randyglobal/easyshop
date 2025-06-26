package org.yearup.models;

import java.math.BigDecimal;

public class OrderLineItem {
    private int orderLineItemId;
    private int orderId;
    private int productId;
    private BigDecimal salesPrice;
    private int quantity;
    private BigDecimal discount;

    public OrderLineItem() {
    }

    public OrderLineItem(BigDecimal salesPrice, BigDecimal discount) {
        this.salesPrice = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
    }

    public OrderLineItem(int orderLineItemId, int orderId, int productId, BigDecimal salesPrice, int quantity, BigDecimal discount) {
        this.orderLineItemId = orderLineItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.salesPrice = salesPrice;
        this.quantity = quantity;
        this.discount = discount;
    }

    public int getOrderLineItemId() {
        return orderLineItemId;
    }

    public void setOrderLineItemId(int orderLineItemId) {
        this.orderLineItemId = orderLineItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }


    @Override
    public String toString() {
        return "OrderLineItem{" +
                "orderLineItemId=" + orderLineItemId +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", salesPrice=" + salesPrice +
                ", quantity=" + quantity +
                ", discount=" + discount +
                '}';
    }
}

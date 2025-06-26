package org.yearup.data;

import org.yearup.models.OrderLineItem;

import java.util.List;

public interface OrderLineItemDao {
    void createOrderLineItem(OrderLineItem item);
    void createOrderLineItems(List<OrderLineItem> items);
}

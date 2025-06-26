package org.yearup.data.mysql;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class MysqlOrderLineItemDao extends  MySqlDaoBase implements OrderLineItemDao {

    public MysqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
//    inserting ust a single order
    public void createOrderLineItem(OrderLineItem item) {
        String sql = " INSERT INTO order_line_items " +
                " (order_id, product_id, sales_price, quantity, discount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, item.getOrderId());
            statement.setInt(2, item.getProductId());
            statement.setBigDecimal(3, item.getSalesPrice());
            statement.setInt(4, item.getQuantity());
            statement.setBigDecimal(5, item.getDiscount());

            statement.executeUpdate();
        }catch (SQLException e){
            System.err.println("Error creating order line item for order ID " + item.getOrderId() + ", product ID " + e.getMessage());
            throw new RuntimeException("Failed to create order line item.", e);
        }

    }

    @Override
//    to avoid inserting a single order multiple times.
    public void createOrderLineItems(List<OrderLineItem> items) {
        String sql = " INSERT INTO order_line_items " +
                " (order_id, product_id, sales_price, quantity, discount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            for (OrderLineItem item : items) {
                statement.setInt(1, item.getOrderId());
                statement.setInt(2, item.getProductId());
                statement.setBigDecimal(3, item.getSalesPrice());
                statement.setInt(4, item.getQuantity());
                statement.setBigDecimal(5, item.getDiscount());
//                groups the order in a batch and submit
                statement.addBatch();
            }
            statement.executeBatch();
        }catch (SQLException e){
            System.err.println("Error creating order multiple items for order ID " + e.getMessage());
            throw new RuntimeException("Failed to create order line item.", e);
        }

    }
}

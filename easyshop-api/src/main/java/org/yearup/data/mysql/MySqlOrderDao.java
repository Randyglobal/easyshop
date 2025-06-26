package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders " +
                "(user_id, date, address, city, state, zip, shipping_amount) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, order.getUserId());
            statement.setDate(2, Date.valueOf(order.getOrderDate()));
            statement.setString(3, order.getAddress());
            statement.setString(4, order.getCity());
            statement.setString(5, order.getState());
            statement.setString(6, order.getZip());
            statement.setBigDecimal(7, order.getShopping_amount());

            statement.executeUpdate();
            try(ResultSet row = statement.getGeneratedKeys()) {
                if (row.next()){
                    int OrderId = row.getInt(1);
                    order.setOrderId(OrderId);
                }
            }
            return order;
        }catch (SQLException e){
            System.err.println("Error creating order for user ID " + order.getUserId() + ": " + e.getMessage());
            throw new RuntimeException("Failed to create order.", e);
        }
    }
}

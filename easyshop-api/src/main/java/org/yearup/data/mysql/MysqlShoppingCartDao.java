package org.yearup.data.mysql;

import org.springframework.stereotype.Repository;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.yearup.data.mysql.MySqlProductDao.mapRow;

@Repository
public class MysqlShoppingCartDao implements ShoppingCartDao {

    private DataSource dataSource;

    public MysqlShoppingCartDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        String sql = "SELECT sc.product_id, sc.quantity, p.name, p.price, p.description, p.image_url, p.category_id " +
                    "FROM shopping_cart sc " +
                    "JOIN products p ON sc.product_id = p.product_id " +
                    "WHERE sc.user_id = ?";

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            try(ResultSet row = statement.executeQuery()) {
                while (row.next()){
                    ShoppingCartItem item = mapRow(row);
                    cart.add(item);
                }
            }
        }catch (SQLException e){
            System.err.println("Server Error" + e);
            throw new RuntimeException(e);
        }
        return cart;
    }

    @Override
    public void addProductToCart(int userId, int productId) {
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1";
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    //    deleting product when quantity is being reduced
    private void clearCartItem(int userId, int productId){
        String sql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProductQuantity(int userId, int productId, int quantity) {
        if (quantity <= 0){
            clearCartItem(userId, productId);
            return;
        }

        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ? ";
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    @Override
    public void clearCart(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCartItem getCartItemByUserIdAndProductId(int userId, int productId) {
        String sql = "SELECT sc.product_id, sc.quantity, p.name, p.price, p.description, p.image_url, p.category_id " +
                     "FROM shopping_cart sc " +
                      "JOIN products p ON sc.product_id = p.product_id " +
                     "WHERE sc.user_id = ? AND sc.product_id = ?";

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);

            try(ResultSet row = statement.executeQuery()) {
                if (row.next()){
                    return mapRow(row);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    private ShoppingCartItem mapRow(ResultSet row) throws SQLException {
        ShoppingCartItem item = new ShoppingCartItem();
        Product product = new Product();

        product.setProductId(row.getInt("product_id"));
        product.setName(row.getString("name"));
        product.setPrice(row.getBigDecimal("price"));
        product.setDescription(row.getString("description"));
        product.setImageUrl(row.getString("image_url"));
        product.setCategoryId(row.getInt("category_id"));

        item.setProduct(product);
        item.setQuantity(row.getInt("quantity"));
        return item;
    }
}

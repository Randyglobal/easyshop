package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    void addProductToCart(int userId, int productId);
    void updateProductQuantity(int userId, int productId, int quantity);
    void clearCart(int userId);
    ShoppingCartItem getCartItemByUserIdAndProductId(int userId, int productId);
    // add additional method signatures here
}

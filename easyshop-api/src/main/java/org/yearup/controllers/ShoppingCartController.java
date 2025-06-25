package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import javax.annotation.security.PermitAll;
import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }



    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            if (user == null){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for authenticated principal.");
            }
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
            if (cart == null){
                return new ShoppingCart();
            }
            return cart;
        }
        catch(Exception e)
        {
            System.err.println("Error getting shopping cart for user: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProductToCart(@PathVariable int productId, Principal principal){
        try {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            if (user == null){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for authenticated principal.");
            }
            int userId = user.getId();

//            checking if product exist
            Product product = productDao.getById(productId);
            if (product == null){
                throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + productId + " not found.");
            }
//            get the current cart
//            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
//            if (cart == null){
//                return new ShoppingCart();
//            }

//            checking if the product exist to increment the quantity
//            ShoppingCartItem existingItem = shoppingCartDao.getCartItemByUserIdAndProductId(userId, productId);
//            if (existingItem == null){
                shoppingCartDao.addProductToCart(userId, productId);
//            }else {
//                shoppingCartDao.updateProductQuantity(userId, productId, existingItem.getQuantity());
//            }
//            returning updated cart
            return shoppingCartDao.getByUserId(userId);

        }catch (Exception e){
            System.err.println("Error adding product to cart: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not add product to cart.");
        }
    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("products/{productId}")
    public ShoppingCart updateProductCart(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal){
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for authenticated principal.");
            }
            int userId = user.getId();
//            checking if the product exist to increment the quantity
            ShoppingCartItem existingItem = shoppingCartDao.getCartItemByUserIdAndProductId(userId, productId);
            if (existingItem == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }
            if (item.getQuantity() < 0){
//                preferred thing to do is to add a delete method to delete the product
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity cannot be negative.");
            }
//                updating the quantity directly if item exist in cart
                shoppingCartDao.updateProductQuantity(userId, productId, item.getQuantity());
//            returning the updated cart
            return shoppingCartDao.getByUserId(userId);

        }catch (ResponseStatusException e){
            throw e;
        }
        catch (Exception e){
            System.err.println("Error adding product to cart: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not update product to cart.");
        }
    }


    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(Principal principal){
        try {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            if (user == null){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for authenticated principal.");
            }
            int userId = user.getId();

            shoppingCartDao.clearCart(userId);
        }catch (Exception e){
            System.err.println("Error adding product to cart: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not delete product to cart.");
        }
    }

}

package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.*;
import org.yearup.models.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("orders")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class OrderController {

    private OrderDao orderDao;
    private OrderLineItemDao orderLineItemDao;
    private ShoppingCartDao shoppingCartDao;
    private ProfileDao profileDao;
    private UserDao userDao;

    public OrderController(OrderDao orderDao, OrderLineItemDao orderLineItemDao, ShoppingCartDao shoppingCartDao, ProfileDao profileDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order checkout(Principal principal){
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for authenticated principal.");
            }
            int userId = user.getId();
//            getting the current user profile info;
            Profile profile = profileDao.getProfileByUserId(userId);
//            default shipping fee
            BigDecimal shippingFee = new BigDecimal("3.00");
            if (profile == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found. Cannot checkout.");
            }

//            get current user
            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            Order newOrder = new Order();
            newOrder.setUserId(userId);
            newOrder.setOrderDate(LocalDate.now());
            newOrder.setAddress(profile.getAddress());
            newOrder.setCity(profile.getCity());
            newOrder.setState(profile.getState());
            newOrder.setZip(profile.getZip());
            newOrder.setShopping_amount(shippingFee);

            newOrder = orderDao.createOrder(newOrder);
            int orderId = newOrder.getOrderId();

            List<OrderLineItem> items = new ArrayList<>();

            for (Map.Entry<Integer, ShoppingCartItem> entry : cart.getItems().entrySet()){
                ShoppingCartItem cartItem = entry.getValue();

                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setOrderId(orderId);
                orderLineItem.setProductId(cartItem.getProductId());
                orderLineItem.setQuantity(cartItem.getQuantity());
                orderLineItem.setSalesPrice(cartItem.getProduct().getPrice());
                orderLineItem.setDiscount(cartItem.getDiscountPercent());

                items.add(orderLineItem);
            }
            orderLineItemDao.createOrderLineItems(items);
            newOrder.setItems(items);

            shoppingCartDao.clearCart(userId);
            return newOrder;
        }catch (Exception e){
            System.err.println("Error during checkout process: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred during checkout. Please try again.");
        }
    }
}

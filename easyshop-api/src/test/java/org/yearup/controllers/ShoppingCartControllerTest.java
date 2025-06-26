package org.yearup.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.math.BigDecimal;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShoppingCartControllerTest {
    @Mock
    private ShoppingCartDao shoppingCartDao;
    @Mock
    private UserDao userDao;
    @Mock
    private ProductDao productDao;
    @Mock // Mock Principal
    private Principal principal;

    // Inject mocks into the controller
    @InjectMocks
    private ShoppingCartController shoppingCartController;

    private User testUser;
    private int testUserId = 1;
    private String testUserName = "testUser";

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);

        // Common setup for tests
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername(testUserName);

        when(principal.getName()).thenReturn(testUserName);
        when(userDao.getByUserName(testUserName)).thenReturn(testUser);
    }

    // --- Tests for getCart() ---

    @Test
    void getCart_shouldReturnShoppingCart_whenUserExistsAndCartNotEmpty() {
        ShoppingCart expectedCart = new ShoppingCart();
        Product product = new Product();
        product.setProductId(1);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));

        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(product);
        item.setQuantity(2);
        expectedCart.add(item);

        when(shoppingCartDao.getByUserId(testUserId)).thenReturn(expectedCart);

        ShoppingCart actualCart = shoppingCartController.getCart(principal);

        assertNotNull(actualCart);
        assertEquals(expectedCart.getItems().size(), actualCart.getItems().size());
        verify(userDao, times(1)).getByUserName(testUserName);
        verify(shoppingCartDao, times(1)).getByUserId(testUserId);
    }


    // --- Tests for addProductToCart() ---
    @Test
    void addProductToCart_shouldThrowUnauthorizedException_whenUserNotFound() {
        int productId = 10;
        when(userDao.getByUserName(testUserName)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            shoppingCartController.addProductToCart(productId, principal);
        });

        verify(userDao, times(1)).getByUserName(testUserName);
        verify(productDao, never()).getById(anyInt());
        verify(shoppingCartDao, never()).addProductToCart(anyInt(), anyInt());
    }

    // --- Tests for updateProductCart() ---
    @Test
    void updateProductCart_shouldThrowUnauthorizedException_whenUserNotFound() {
        int productId = 1;
        ShoppingCartItem itemToUpdate = new ShoppingCartItem();
        itemToUpdate.setQuantity(3);
        when(userDao.getByUserName(testUserName)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            shoppingCartController.updateProductCart(productId, itemToUpdate, principal);
        });

        assertEquals("User not found for authenticated principal.", exception.getReason());
        verify(userDao, times(1)).getByUserName(testUserName);
        verify(shoppingCartDao, never()).getCartItemByUserIdAndProductId(anyInt(), anyInt());
    }
    // --- Tests for clearCart() ---
    @Test
    void clearCart_shouldClearCartSuccessfully() {
        doNothing().when(shoppingCartDao).clearCart(testUserId);

        assertDoesNotThrow(() -> shoppingCartController.clearCart(principal));

        verify(userDao, times(1)).getByUserName(testUserName);
        verify(shoppingCartDao, times(1)).clearCart(testUserId);
    }
}
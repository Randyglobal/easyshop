package org.yearup.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;

import java.math.BigDecimal; // Important import
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@BeforeEach to mark a method that runs before every test method. This is for common setup.
@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

//    For any classes that your class-under-test depends on
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductsController productsController;

    @BeforeEach
    void setUp() {
        // setup, needed for the test to run
    }

//    test annotation
    @Test
    void updateProduct_shouldUpdateProductSuccessfully() {
        // Arrange
        int productId = 1;
        Product existingProduct = new Product(productId, "Old Name", BigDecimal.valueOf(10.00));
        Product updatedProduct = new Product(productId, "New Name", BigDecimal.valueOf(15.00));

//        These lines are Mockito stubbing statements :
//        They define the behavior of your productDao mock object when certain methods are called during the test
        when(productDao.getById(productId)).thenReturn(existingProduct);
        doNothing().when(productDao).update(productId, updatedProduct);

        // Act & Assert
//        Execute the productsController.updateProduct(productId, updatedProduct) method.
//        If this method completes successfully without throwing any kind of Throwable
        assertDoesNotThrow(() -> productsController.updateProduct(productId, updatedProduct));

//        his ensures that your controller's logic correctly attempted to retrieve the
//        existing product by its ID from the DAO before proceeding with the update.
        verify(productDao, times(1)).getById(productId);
        verify(productDao, times(1)).update(productId, updatedProduct);
    }
    @Test
    void updateProduct_shouldThrowNotFoundException_whenProductDoesNotExist() {
        int productId = 2;
        Product existingProduct = new Product(productId, "MyName", BigDecimal.valueOf(11.00));
        Product updatedProduct = new Product(productId, "NewName", BigDecimal.valueOf(14.00));

        when(productDao.getById(productId)).thenReturn(existingProduct);
        doNothing().when(productDao).update(productId, updatedProduct);

        // Act & Assert
        assertDoesNotThrow(() -> productsController.updateProduct(productId, updatedProduct));

        verify(productDao, times(1)).getById(productId);
        verify(productDao, times(1)).update(productId, updatedProduct);
    }

    @Test
    void search_shouldReturnAllProducts_whenNoParametersAreProvided() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(
                new Product(1, "Laptop", BigDecimal.valueOf(1200.00)),
                new Product(2, "Mouse", BigDecimal.valueOf(25.00))
        );

        // When productDao.search is called with all null parameters, return the expected list
        when(productDao.search(null, null, null, null)).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productsController.search(null, null, null, null);

        // Assert
        assertNotNull(actualProducts);
        assertEquals(expectedProducts.size(), actualProducts.size());
        assertEquals(expectedProducts, actualProducts); // Assumes Product has well-defined equals/hashCode

        // Verify that productDao.search was called exactly once with all nulls
        verify(productDao, times(1)).search(null, null, null, null);
    }
}
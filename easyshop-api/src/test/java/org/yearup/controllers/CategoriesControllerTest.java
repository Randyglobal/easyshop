package org.yearup.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CategoriesControllerTest {

//    Mock instance of categoryDao
    @Mock
    private CategoryDao categoryDao;
    //    Mock instance of ProductDao
    @Mock
    private ProductDao productDao;

//    Injecting them in categoryController
    @InjectMocks
    private CategoriesController categoriesController;

    // Optional setup method, however good for initialization
    @BeforeEach
    void setUp() {
    }

    @Test
    void getAll_shouldReturnAllCategoriesSuccessfully() {
        List<Category> expectedCategories = Arrays.asList(
                new Category(1, "Electronics", "Devices"),
                new Category(2, "Books", "Reading materials")
        );
//        returns list
        when(categoryDao.getAllCategories()).thenReturn(expectedCategories);

        List<Category> actualCategories = categoriesController.getAll();

        assertNotNull(actualCategories);
        assertEquals(expectedCategories.size(), actualCategories.size());
        assertEquals(expectedCategories, actualCategories);

        // Verify that the DAO method was called exactly once
        verify(categoryDao, times(1)).getAllCategories();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoCategoriesExist() {
        when(categoryDao.getAllCategories()).thenReturn(Collections.emptyList());

        List<Category> actualCategories = categoriesController.getAll();

        assertNotNull(actualCategories);
        assertTrue(actualCategories.isEmpty());

        verify(categoryDao, times(1)).getAllCategories();
    }

    @Test
    void getById_shouldReturnCategory_whenCategoryExists() {
        int categoryId = 1;
        Category expectedCategory = new Category(categoryId, "Electronics", "Devices");
        when(categoryDao.getById(categoryId)).thenReturn(expectedCategory);
        Category actualCategory = categoriesController.getById(categoryId);

        assertNotNull(actualCategory);
        assertEquals(expectedCategory, actualCategory);

        verify(categoryDao, times(1)).getById(categoryId);
    }

    @Test
    void getProductsById_shouldReturnProducts_whenCategoryExistsAndHasProducts() {
        int categoryId = 1;
        Category existingCategory = new Category(categoryId, "Electronics", "Devices");
        List<Product> expectedProducts = Arrays.asList(
                new Product(101, "Laptop", BigDecimal.valueOf(1200.00)),
                new Product(102, "Charger", BigDecimal.valueOf(50.00))
        );

        when(categoryDao.getById(categoryId)).thenReturn(existingCategory); // Category exists
        when(productDao.listByCategoryId(categoryId)).thenReturn(expectedProducts); // Products exist for category
        List<Product> actualProducts = categoriesController.getProductsById(categoryId);

        assertNotNull(actualProducts);
        assertEquals(expectedProducts.size(), actualProducts.size());
        assertEquals(expectedProducts, actualProducts);

        verify(categoryDao, times(1)).getById(categoryId);
        verify(productDao, times(1)).listByCategoryId(categoryId);
    }

    @Test
    void updateCategory_shouldUpdateCategorySuccessfully() {
        // Arrange
        int categoryId = 1;
        Category existingCategory = new Category(categoryId, "Old Name", "Old Desc");
        Category updatedCategory = new Category(categoryId, "New Name", "New Desc");

        when(categoryDao.getById(categoryId)).thenReturn(existingCategory);
        doNothing().when(categoryDao).update(categoryId, updatedCategory);

        // Act & Assert
        assertDoesNotThrow(() -> categoriesController.updateCategory(categoryId, updatedCategory));

        verify(categoryDao, times(1)).getById(categoryId);
        verify(categoryDao, times(1)).update(categoryId, updatedCategory);
    }

    @Test
    void deleteCategory_shouldDeleteCategorySuccessfully() {
        // Arrange
        int categoryId = 1;
        Category existingCategory = new Category(categoryId, "To Delete", "Will be gone");
        when(categoryDao.getById(categoryId)).thenReturn(existingCategory);
        doNothing().when(categoryDao).delete(categoryId);

        // Act & Assert (assertDoesNotThrow for void methods)
        assertDoesNotThrow(() -> categoriesController.deleteCategory(categoryId));

        verify(categoryDao, times(1)).getById(categoryId);
        verify(categoryDao, times(1)).delete(categoryId);
    }

}
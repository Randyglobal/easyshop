package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
@RestController
// add the annotation to make this controller the endpoint for the following url
@RequestMapping("categories")
// add annotation to allow cross site origin requests
//tells the browser that it's safe for your backend to accept requests from origins other than its own
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;

    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // add the appropriate annotation for a get action
    @GetMapping("")
    public List<Category> getAll()
    {
        // find and return all categories
        try {
            return categoryDao.getAllCategories();
        }catch (Exception e){
            System.err.println("Error fetching all categories: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not retrieve categories.");
        }
    }

    // add the appropriate annotation for a get action
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        try {
            Category category = categoryDao.getById(id);
            if (category == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id);
            }
            return category;

        }catch (Exception e){
            System.err.println("Error fetching categories: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not retrieve categories.");
        }
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId
        try {
            Category category = categoryDao.getById(categoryId);
            if (category == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + categoryId);
            }
            return productDao.listByCategoryId(categoryId);
        }catch (Exception e){
            System.err.println("Error fetching product for categories: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not retrieve categories.");
        }
    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category
        try {
            return categoryDao.create(category);
        }catch (Exception e){
            System.err.println("Error adding categories: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not retrieve categories.");
        }
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
        try {
            // Ensuring ID matches
            if (category.getCategoryId() == 0){
                category.setCategoryId(id);
            }else if(category.getCategoryId() != id){
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID in path does not match ID in request body.");
            }
            Category existingCategory = categoryDao.getById(id);
//            error exception
            if (existingCategory == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category to be updated not found");
            }
            categoryDao.update(id, category);
        }catch (ResponseStatusException e){
            throw new RuntimeException(e);
        }
        catch (Exception e){
            System.err.println("Error updating categories: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not retrieve categories.");
        }
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        try {
            Category existingCategory = categoryDao.getById(id);
//            error exception
            if (existingCategory == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category to be deleted not found");
            }
            categoryDao.delete(id);
        }catch (Exception e){
            System.err.println("Error deleting categories: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not retrieve categories.");
        }
    }
}

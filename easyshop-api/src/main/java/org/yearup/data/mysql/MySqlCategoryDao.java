package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, name, description FROM categories ";
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet row = statement.executeQuery();

            while (row.next()){
                Category category = mapRow(row);
                categories.add(category);
            }
        }catch (SQLException e){
            System.err.println("Error in getAllCategories: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId){
        // get category by id
        String sql = "SELECT category_id, name, description From categories WHERE category_id = ?";
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();

            if (row.next()){
                return mapRow(row);
            }
        }catch (SQLException e){
            System.err.println("Error in getById for categoryId " + categoryId + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            int rowAffected = statement.executeUpdate();
            if (rowAffected > 0){
                ResultSet row = statement.getGeneratedKeys();
                if (row.next()){
                    int newCategoryId = row.getInt(1);
                    return getById(newCategoryId);
                }
            }
            throw new RuntimeException("Category creation failed, no ID obtained.");
        }catch (SQLException e){
            System.err.println("Error in create category: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String sql = "UPDATE categories SET name = ?, description = ? WHERE categories_id = ?";
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId); // Using the ID from the path

            int rowAffected = statement.executeUpdate();
            if (rowAffected == 0){
                System.out.println("No category of type " + categoryId + " found");
            }
        }catch (SQLException e){
            System.err.println("Error in update categoryId " + categoryId + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId); // Using the ID from the path

            int rowAffected = statement.executeUpdate();
            if (rowAffected == 0){
                System.out.println("No category of type " + categoryId + " found");
            }
        }catch (SQLException e){
            System.err.println("Error in update categoryId " + categoryId + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}

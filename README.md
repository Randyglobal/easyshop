# easyshop
FullStack Capstone

# EasyShop E-Commerce API and Site - Capstone Project

This project is the third capstone for Java Development, focusing on building and enhancing an e-commerce application named EasyShop. As a backend developer, my role involves modifying an existing Spring Boot API project that serves as the backend for the EasyShop website. The application uses a MySQL database for data storage.

## Table of Contents

* [Project Description](#project-description)
* [Features](#features)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)
* [API Endpoints](#api-endpoints)
* [Bug Fixes](#bug-fixes)
* [Demonstration](#demonstration)
* [Future Enhancements](#future-enhancements)
* [Interesting Code Snippet](#interesting-code-snippet)
* [License](#license)

## Project Description

EasyShop is an online store that allows users to browse products in various categories, add them to a shopping cart, and check out to order the products. This capstone project involves working on Version 2 of the EasyShop API, which includes fixing existing bugs and developing several new features. The primary focus is on the backend Spring Boot Java API, with Postman heavily used for testing API endpoints and logic. A front-end website project is also available in the starter code for visual testing.

## Features

### Version 1 (Existing Functionality)

* User registration and login 
* Displaying products by category 
* Search for or filter the products list 

### Version 2 (New Features & Enhancements)

#### Categories Controller 
* **Create Category (POST)**: Allows administrators to add new product categories.
* **Retrieve All Categories (GET)**: Fetches a list of all available categories.
* **Retrieve Category by ID (GET)**: Retrieves details for a specific category.
* **Update Category (PUT)**: Enables administrators to modify existing category details.
* **Delete Category (DELETE)**: Allows administrators to remove categories.

#### Shopping Cart (Optional Phase 3) 
* **Add Item to Cart (POST)**: Users can add products to their shopping cart. If the product is already in the cart, its quantity is increased by 1.
* **View Shopping Cart (GET)**: Displays the current user's shopping cart, including all items (products).
* **Update Cart Item Quantity (PUT)**: Allows users to change the quantity of a specific product in their cart.
* **Clear Shopping Cart (DELETE)**: Empties the entire shopping cart for the current user.

#### User Profile (Optional Phase 4) 
* **View User Profile (GET)**: Users can retrieve their personal profile information.
* **Update User Profile (PUT)**: Users can modify and update their profile details.

#### Checkout (Optional Phase 5) 
* **Create Order from Cart (POST)**: Converts the user's shopping cart into a new order, creates order line items, and then clears the cart.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* Java Development Kit (JDK) 
* Apache Maven
* MySQL Database 
* MySQL Workbench 
* Postman (for API testing) 
* Git 

### Installation

1.  **Clone the repository:**
    ```bash
    git clone <your-github-repo-url> C:\pluralsight\LearnToCode_Capstones\<your-project-name>
    ```
    Replace `<your-github-repo-url>` with the URL of your new GitHub repository and `<your-project-name>` with the name you chose for your project.

2.  **Database Setup:**
    * Open `create_database.sql` located in the `database` folder in MySQL Workbench.
    * Execute the script to create the `easyshop` database.
    * The script includes sample products and three demo users: `user`, `admin`, and `george`. The password for all demo users is `password`.

3.  **Run the Spring Boot Application:**
    * Navigate to the root directory of your cloned Spring Boot API project.
    * Build the project using Maven:
        ```bash
        mvn clean install
        ```
    * Run the application:
        ```bash
        mvn spring-boot:run
        ```
    The API should now be running on `http://localhost:8080`.

## API Endpoints

The API includes an `AuthenticationController` to allow new users to register and existing users to login.

* **Register:** `POST http://localhost:8080/register` 
    ```json
    {
        "username": "admin",
        "password": "password",
        "confirmPassword": "password",
        "role": "ADMIN"
    }
    ```
* **Login:** `POST http://localhost:8080/login` 
    ```json
    {
        "username": "admin",
        "password": "password"
    }
    ```
    Upon successful login, the API returns user information and a JWT authentication token , which needs to be included in Postman requests for authenticated endpoints.

### Categories Endpoints 

| VERB   | URL                               | BODY      |
| :----- | :-------------------------------- | :-------- |
| `GET`  | `http://localhost:8080/categories`  | `NO body` |
| `GET`  | `http://localhost:8080/categories/1`| `NO body` |
| `POST` | `http://localhost:8080/categories`  | `Category`|
| `PUT`  | `http://localhost:8080/categorids/1`| `Category`|
| `DELETE`| `http://localhost:8080/categorids/1`| `NO body` |

### Products Endpoints 

| VERB   | URL                               | BODY      |
| :----- | :-------------------------------- | :-------- |
| `GET`  | `http://localhost:8080/products`  | `NO body` |
| `GET`  | `http://localhost:8080/products/1`| `NO body` |
| `POST` | `http://localhost:8080/products`  | `Category`|
| `PUT`  | `http://localhost:8080/products/1`| `Category`|
| `DELETE`| `http://localhost:8080/products/1`| `NO body` |

### Shopping Cart Endpoints (Optional Phase 3) 

| VERB   | URL                                   | BODY             |
| :----- | :------------------------------------ | :--------------- |
| `GET`  | `http://localhost:8080/cart`          | `NO body`        |
| `POST` | `http://localhost:8080/cart/products/15`| `NO body`        |
| `PUT`  | `http://localhost:8080/cart/products/15`| `has body` (`{"quantity": 3}`)  |
| `DELETE`| `http://localhost:8080/cart`          | `NO body`        |

### User Profile Endpoints (Optional Phase 4) 

| VERB   | URL                               | BODY      |
| :----- | :-------------------------------- | :-------- |
| `GET`  | `http://localhost:8080/profile`   | `NO body` |
| `PUT`  | `http://localhost:8080/profile`   | `Profile body` |

### Orders Endpoints (Optional Phase 5) 

| VERB   | URL                               | BODY      |
| :----- | :-------------------------------- | :-------- |
| `POST` | `http://localhost:8080/orders`    | `NO body` |

## Bug Fixes

As part of this project, I addressed two main bugs in the existing API:

* **Product Search/Filter:** The search functionality was returning incorrect results. I've implemented fixes to ensure accurate filtering by category, price range, and color. Unit tests were included as part of this process.
* **Product Duplication on Update:** Previously, updating a product would create a new entry instead of modifying the existing one. This bug has been resolved, allowing administrators to safely update product information.

## Demonstration

During the project demonstration, I will:

* Run the front-end web application and walk through the ordering process, highlighting the newly added features.
* Demonstrate how Postman can be used to interact with and test the API endpoints.
* Showcase an interesting piece of code that I wrote during the development process.

## Future Enhancements

While not part of the current scope, future versions of EasyShop could include: 

* Order History for Users
* Product Reviews and Ratings
* Admin Dashboard
* Payment Gateway Integration
* Wishlist Functionality

## Interesting Code Snippet

*(will be filled in during the demonstration with a relevant code example)*



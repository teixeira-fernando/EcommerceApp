package com.ecommerceapp.inventory.unit.repository;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

@DataMongoTest
@ExtendWith(MongoSpringExtension.class)
public class InventoryRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private InventoryRepository repository;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Test
    @DisplayName("Save product - success")
    @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Product")
    void testSaveProductSuccess() {
        // Create a test Product
        String productName = "Dark Souls 3";
        Integer quantity = 20;
        Category category = Category.VIDEOGAMES;

        Product productToBeInserted = new Product(productName, quantity, category);

        // Persist the product to MongoDB
        Product savedProduct = repository.save(productToBeInserted);

        // Retrieve the product
        Optional<Product> loadedProduct = repository.findById(savedProduct.getId());

        // Validations
        Assertions.assertTrue(loadedProduct.isPresent());
        loadedProduct.ifPresent(
                product -> {
                    Assertions.assertEquals(productName, product.getName());
                    Assertions.assertEquals(
                            quantity, product.getQuantity(), "Product quantity should be " + quantity);
                    Assertions.assertEquals(
                            category, product.getCategory(), "Product category should be " + category.toString());
                });
    }

    @Test
    @DisplayName("Find All Products - Success")
    @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Product")
    void testFindAllProductsSuccess() {
        List<Product> products = repository.findAll();
        products.forEach(System.out::println);
        Assertions.assertEquals(2, products.size(), "Should be two products in the database");
    }

    @Test
    @DisplayName("Find Product - Success")
    @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
    void testFindProductSuccess() {
        String productName = "Dark Souls 3";
        Integer quantity = 20;
        Category category = Category.VIDEOGAMES;

        Product newProduct = new Product(productName, quantity, category);

        // Persist the product to MongoDB
        Product savedProduct = repository.save(newProduct);

        Optional<Product> returnedProduct = repository.findById(newProduct.getId());
        Assertions.assertTrue(returnedProduct.isPresent());

        returnedProduct.ifPresent(
                p -> {
                    Assertions.assertEquals(p.getName(), newProduct.getName());
                    Assertions.assertEquals(p.getQuantity(), newProduct.getQuantity());
                    Assertions.assertEquals(p.getCategory(), newProduct.getCategory());
                });
    }

    @Test
    @DisplayName("Find Product - Not Found")
    @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Product")
    void testFindProductNotFound() {
        Optional<Product> product = repository.findById("99");
        Assertions.assertFalse(product.isPresent());
    }

    @Test
    @DisplayName("Test Update Success")
    @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Product")
    void testUpdateSuccess() {
        // Retrieve the first product
        Optional<Product> product = Optional.ofNullable(repository.findAll().get(0));
        Assertions.assertTrue(product.isPresent(), "Product should be present");

        // Add an entry to the review and save
        Product productToUpdate = product.get();
        productToUpdate.setName("Samsung TV 42º Led");
        repository.save(productToUpdate);

        // Retrieve the product again and validate it
        Optional<Product> updatedProduct = repository.findById(product.get().getId());
        Assertions.assertTrue(updatedProduct.isPresent(), "Product should be present");
        Assertions.assertEquals(
                "Samsung TV 42º Led", updatedProduct.get().getName(), "The product name should be updated");
    }

    @Test
    @DisplayName("Test Delete Success")
    @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Product")
    void testDeleteSuccess() {
        repository.deleteById("1");

        // Confirm that it is no longer in the database
        Optional<Product> returnedProduct = repository.findById("1");
        Assertions.assertFalse(returnedProduct.isPresent());
    }
}

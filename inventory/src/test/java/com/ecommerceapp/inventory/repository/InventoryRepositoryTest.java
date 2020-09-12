package com.ecommerceapp.inventory.repository;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@DataMongoTest
@ExtendWith(MongoSpringExtension.class)
public class InventoryRepositoryTest {

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private InventoryRepository repository;

  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Test
  @DisplayName("Save product - success")
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
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
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
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
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testFindProductNotFound() {
    Optional<Product> product = repository.findById("99");
    Assertions.assertFalse(product.isPresent());
  }

  @Test
  @DisplayName("Test Update Success")
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testUpdateSuccess() {
    // Retrieve the first product
    Optional<Product> product = Optional.ofNullable(repository.findAll().get(0));
    Assertions.assertTrue(product.isPresent(), "Review should be present");

    // Add an entry to the review and save
    Product productToUpdate = product.get();
    productToUpdate.setName("Samsung TV 42º Led");
    repository.save(productToUpdate);

    // Retrieve the review again and validate that it now has 4 entries
    Optional<Product> updatedReview = repository.findById(product.get().getId());
    Assertions.assertTrue(updatedReview.isPresent(), "Review should be present");
    Assertions.assertEquals(
        "Samsung TV 42º Led", updatedReview.get().getName(), "The product name should be updated");
  }

  @Test
  @DisplayName("Test Delete Success")
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testDeleteSuccess() {
    Optional<Product> product = Optional.ofNullable(repository.findAll().get(0));
    repository.deleteById(product.get().getId());

    // Confirm that it is no longer in the database
    Optional<Product> returnedProduct = repository.findById(product.get().getId());
    Assertions.assertFalse(returnedProduct.isPresent());
  }
}
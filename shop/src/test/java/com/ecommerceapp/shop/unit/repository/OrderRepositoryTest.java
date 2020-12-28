package com.ecommerceapp.shop.unit.repository;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.repository.OrderRepository;
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
class OrderRepositoryTest {

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private OrderRepository repository;

  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Test
  @DisplayName("Save order - success")
  @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
  void testSaveOrderSuccess() {
    // Arrange
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    Order orderToBeInserted = new Order();
    orderToBeInserted.getProducts().add(new Product(productName, quantity, category));

    // Persist the order to MongoDB
    Order savedOrder = repository.save(orderToBeInserted);

    // Validations
    Assertions.assertEquals(orderToBeInserted.getId(), savedOrder.getId());
    Assertions.assertEquals(
        orderToBeInserted.getCreateDate(),
        savedOrder.getCreateDate(),
        "Order createdDate should be " + orderToBeInserted.getCreateDate());
    Assertions.assertEquals(
        orderToBeInserted.getProducts().size(),
        savedOrder.getProducts().size(),
        "Order products list should be equals");
  }

  @Test
  @DisplayName("Find All Orders - Success")
  @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
  void testFindAllOrdersSuccess() {
    List<Order> orders = repository.findAll();
    Assertions.assertEquals(2, orders.size(), "Should be two orders in the database");
  }

  @Test
  @DisplayName("Find Order - Success")
  @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
  void testFindOrderSuccess() {
    // Arrange
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    Order orderToBeInserted = new Order();
    orderToBeInserted.getProducts().add(new Product(productName, quantity, category));

    // Persist the order to MongoDB
    Order savedOrder = repository.save(orderToBeInserted);

    // Retrieve the order
    Optional<Order> loadedOrder = repository.findById(savedOrder.getId());

    // Validations
    Assertions.assertTrue(loadedOrder.isPresent());
    loadedOrder.ifPresent(
        order -> {
          Assertions.assertEquals(orderToBeInserted.getId(), order.getId());
          Assertions.assertEquals(
              orderToBeInserted.getCreateDate(),
              order.getCreateDate(),
              "Order createdDate should be " + orderToBeInserted.getCreateDate());
          Assertions.assertEquals(
              orderToBeInserted.getProducts().size(),
              order.getProducts().size(),
              "Order products list should be equals");
        });
  }

  @Test
  @DisplayName("Find Order - Not Found")
  @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
  void testFindOrderNotFound() {
    Optional<Order> order = repository.findById("99");
    Assertions.assertFalse(order.isPresent());
  }

  @Test
  @DisplayName("Test Update Success")
  @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
  void testUpdateSuccess() {
    // Retrieve the first product
    Optional<Order> order = Optional.ofNullable(repository.findAll().get(0));
    Assertions.assertTrue(order.isPresent(), "Order should be present");

    // Add an entry to the review and save
    Order orderToUpdate = order.get();
    orderToUpdate.getProducts().add(new Product("test", 12, Category.BOOKS));
    repository.save(orderToUpdate);

    // Retrieve the order again and validate it
    Optional<Order> updatedOrder = repository.findById(order.get().getId());
    Assertions.assertTrue(updatedOrder.isPresent(), "Order should be present");
    Assertions.assertEquals(
        2, updatedOrder.get().getProducts().size(), "The order should contain 2 products");
  }

  @Test
  @DisplayName("Test Delete Success")
  @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
  void testDeleteSuccess() {
    repository.deleteById("1");

    // Confirm that it is no longer in the database
    Optional<Order> returnedOrder = repository.findById("1");
    Assertions.assertFalse(returnedOrder.isPresent());
  }
}

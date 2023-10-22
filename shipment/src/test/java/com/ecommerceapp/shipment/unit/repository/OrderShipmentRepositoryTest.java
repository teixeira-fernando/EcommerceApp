package com.ecommerceapp.shipment.unit.repository;

import com.ecommerceapp.domain.Category;
import com.ecommerceapp.domain.Order;
import com.ecommerceapp.domain.Product;
import com.ecommerceapp.shipment.domain.Location;
import com.ecommerceapp.shipment.domain.OrderShipment;
import com.ecommerceapp.shipment.repository.OrderShipmentRepository;
import java.util.List;
import java.util.Optional;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@DataMongoTest
@ExtendWith(MongoSpringExtension.class)
class OrderShipmentRepositoryTest {

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private OrderShipmentRepository repository;

  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Test
  @DisplayName("Save order shipment - success")
  @MongoDataFile(
      value = "sample.json",
      classType = OrderShipment.class,
      collectionName = "OrderShipment")
  void testSaveOrderShipmentSuccess() {
    // Create a test Product
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;
    Product productToBeInserted = new Product(productName, quantity, category);

    // create an order
    Order order = new Order(List.of(productToBeInserted));

    // create an order shipment
    Location location = new Location("street", 123, "postalcode", "city", "state");
    OrderShipment orderShipment = new OrderShipment(order, LocalDate.now(), location);

    // Persist the order shipment to MongoDB
    OrderShipment savedOrderShipment = repository.save(orderShipment);

    // Retrieve the order shipment
    Optional<OrderShipment> loadedOrderShipment = repository.findById(savedOrderShipment.getId());

    // Validations
    Assertions.assertTrue(loadedOrderShipment.isPresent());
    loadedOrderShipment.ifPresent(
        shipment -> {
          Assertions.assertFalse(shipment.getOrder().getProducts().isEmpty());
          Assertions.assertEquals(
              productName,
              shipment.getOrder().getProducts().get(0).getName(),
              "Product name should be " + productName);
          Assertions.assertEquals(
              quantity,
              shipment.getOrder().getProducts().get(0).getQuantity(),
              "Product quantity should be " + quantity);
          Assertions.assertEquals(
              category,
              shipment.getOrder().getProducts().get(0).getCategory(),
              "Product category should be " + category.toString());
        });
  }

  @Test
  @DisplayName("Find All Order Shipments - Success")
  @MongoDataFile(
      value = "sample.json",
      classType = OrderShipment.class,
      collectionName = "OrderShipment")
  void testFindAllOrderShipmentSuccess() {
    List<OrderShipment> orderShipments = repository.findAll();
    orderShipments.forEach(System.out::println);
    Assertions.assertEquals(
        1, orderShipments.size(), "Should be one orderShipment in the database");
  }

  @Test
  @DisplayName("Find order shipment - success")
  @MongoDataFile(
      value = "sample.json",
      classType = OrderShipment.class,
      collectionName = "OrderShipment")
  void testFindOrderShipmentSuccess() {
    String orderShipmentId = "5fc97d7b5fd868721138b0ec";

    // Retrieve the order shipment
    Optional<OrderShipment> loadedOrderShipment = repository.findById(orderShipmentId);

    // Validations
    Assertions.assertTrue(loadedOrderShipment.isPresent());
    loadedOrderShipment.ifPresent(
        shipment -> {
          Assertions.assertFalse(shipment.getOrder().getProducts().isEmpty());
        });
  }
}

package com.ecommerceapp.shipment.unit.service;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shipment.model.Location;
import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.repository.OrderShipmentRepository;
import com.ecommerceapp.shipment.service.ShipmentService;
import com.ecommerceapp.shipment.service.kafka.MessageListenerShipment;
import com.ecommerceapp.shop.model.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ShipmentService.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ShipmentServiceTest {

  /** The service that we want to test. */
  @Autowired private ShipmentService service;

  @MockBean private OrderShipmentRepository repository;

  @MockBean private MessageListenerShipment messageListenerShipment;

  @Test
  @DisplayName("findById - success")
  void testFindByIdSuccess() {
    // Arrange: Setup our mock
    // Create a test Product
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;
    Product productToBeInserted = new Product(productName, quantity, category);

    // create an order
    Order order = new Order(List.of(productToBeInserted));

    // create an order shipment
    Location location = new Location("street", 123, "postalcode", "city", "state");
    OrderShipment orderShipment = new OrderShipment("1", order, LocalDate.now(), location);

    doReturn(Optional.of(orderShipment)).when(repository).findById(orderShipment.getId());

    // Act: Call the service method findById
    OrderShipment returnedOrderShipment = service.findById(orderShipment.getId());

    // Assert: verify the returned order shipment
    Assertions.assertNotNull(returnedOrderShipment, "OrderShipment was not found");
    Assertions.assertEquals(
        returnedOrderShipment, orderShipment, "OrderShipment should be the same");
  }

  @Test
  @DisplayName("findById - Not Found")
  void testFindByIdNotFound() {

    when(repository.findById(any())).thenReturn(Optional.empty());

    Assertions.assertThrows(
        NoSuchElementException.class,
        () -> {
          service.findById("9999");
        });
  }

  @Test
  @DisplayName("test findAll - Success")
  void testFindAllSuccess() {
    // arrange
    Location location = new Location("street", 123, "postalcode", "city", "state");
    OrderShipment orderShipment = new OrderShipment("1", new Order(), LocalDate.now(), location);
    OrderShipment orderShipment2 = new OrderShipment("2", new Order(), LocalDate.now(), location);

    doReturn(List.of(orderShipment, orderShipment2)).when(repository).findAll();

    // act
    List<OrderShipment> orderShipments = service.findAll();

    // assert
    Assertions.assertEquals(orderShipments.size(), 2);
    Assertions.assertEquals(orderShipments.get(0), orderShipment);
    Assertions.assertEquals(orderShipments.get(1), orderShipment2);
  }

  @Test
  @DisplayName("test create order shipment - success")
  void testCreateOrderShipment() {
    // Arrange: Setup our mock

    // Create a test Product
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;
    Product productToBeInserted = new Product(productName, quantity, category);

    // create an order
    Order order = new Order(List.of(productToBeInserted));

    // create an order shipment
    Location location = new Location("street", 123, "postalcode", "city", "state");
    OrderShipment orderShipment = new OrderShipment("1", order, LocalDate.now(), location);

    doReturn(orderShipment).when(repository).save(any());

    // act
    OrderShipment returnedShipment = service.createOrderShipment(order);

    // assert
    Assertions.assertEquals(orderShipment, returnedShipment);
  }
}

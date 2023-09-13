package com.ecommerceapp.shipment.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.ecommerceapp.shipment.model.Location;
import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.model.inventory.Category;
import com.ecommerceapp.shipment.model.inventory.Product;
import com.ecommerceapp.shipment.model.shop.Order;
import com.ecommerceapp.shipment.repository.OrderShipmentRepository;
import com.ecommerceapp.shipment.service.ShipmentService;
import com.ecommerceapp.shipment.service.kafka.MessageListenerShipment;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {ShipmentService.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class ShipmentServiceTest {

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
    Assertions.assertEquals(2, orderShipments.size());
    Assertions.assertEquals(orderShipment, orderShipments.get(0));
    Assertions.assertEquals(orderShipment2, orderShipments.get(1));
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

package com.ecommerceapp.shop.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.ecommerceapp.domain.Category;
import com.ecommerceapp.domain.Order;
import com.ecommerceapp.domain.Product;
import com.ecommerceapp.shop.dto.request.OrderDto;
import com.ecommerceapp.shop.exceptions.EmptyOrderException;
import com.ecommerceapp.shop.exceptions.StockUpdateException;
import com.ecommerceapp.shop.repository.OrderRepository;
import com.ecommerceapp.shop.service.InventoryClient;
import com.ecommerceapp.shop.service.OrderService;
import com.ecommerceapp.shop.service.kafka.MessageProducer;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {OrderService.class, InventoryClient.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class OrderServiceTest {

  /** The service that we want to test. */
  @Autowired private OrderService service;

  @MockBean private OrderRepository repository;

  @MockBean private InventoryClient inventoryClient;

  @MockBean private MessageProducer producer;

  @Test
  @DisplayName("findById - success")
  void testFindByIdSuccess() {
    // Arrange: Setup our mock
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Order order = new Order(new ArrayList<>());
    order.getProducts().add(new Product(productName, quantity, category));
    doReturn(Optional.of(order)).when(repository).findById(order.getId());

    // Act: Call the service method findById
    Order returnedOrder = service.findById(order.getId());

    // Assert: verify the returned product
    Assertions.assertNotNull(returnedOrder, "Order was not found");
    Assertions.assertEquals(returnedOrder, order, "Order should be the same");
  }

  @Test
  @DisplayName("findById not found")
  void testFindByIdNotFound() {
    // Arrange: Setup our mock
    doReturn(Optional.empty()).when(repository).findById("1");

    // Verify if findById throws this exception
    Assertions.assertThrows(
        NoSuchElementException.class,
        () -> {
          service.findById("1");
        });
  }

  @Test
  @DisplayName("findAll Success")
  void testFindAllSuccess() {
    // Arrange: Setup our mock
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    Product product2 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    ArrayList<Product> products = new ArrayList<>();
    products.add(product1);
    products.add(product2);
    Order order1 = new Order(products);
    Order order2 = new Order();
    doReturn(Arrays.asList(order1, order2)).when(repository).findAll();

    // Act: Call the service method findAll
    List<Order> orders = service.findAll();

    // Assert the response
    Assertions.assertEquals(2, orders.size(), "The product list should return 2 items");
  }

  @Test
  @DisplayName("createOrder sucess")
  void testCreateOrderSuccess() throws URISyntaxException, EmptyOrderException {
    // Arrange: Setup our mock
    Order order = new Order();
    order.getProducts().add(new Product("1", "test", 50, Category.BOOKS));

    when(inventoryClient.checkIfProductExists(any())).thenReturn(true);
    when(inventoryClient.checkIfProductHaveEnoughStock(any(), anyInt())).thenReturn(true);
    doNothing().when(inventoryClient).updateStock(any(), any());
    doReturn(order).when(repository).save(any());

    // Act: Call the service method create product
    Order createdOrder = service.createOrder(new OrderDto(order.getProducts()));

    // Assert: verify the returned product
    Assertions.assertNotNull(createdOrder, "The saved order should not be null");
    Assertions.assertEquals(createdOrder, order, "Order should be the same");
  }

  @Test
  @DisplayName("createOrder Empty - should return an error")
  void testCreateOrderEmpty() {
    Order order = new Order();

    Assertions.assertThrows(
        EmptyOrderException.class,
        () -> {
          service.createOrder(new OrderDto(order.getProducts()));
        });
  }

  @Test
  @DisplayName("createOrder Product not found - should return an error")
  void testCreateOrderProductNotFound() throws URISyntaxException {
    // Arrange: Setup our mock
    Order order = new Order();
    order.getProducts().add(new Product("1", "test", 50, Category.BOOKS));

    when(inventoryClient.checkIfProductExists(any())).thenReturn(false);

    Assertions.assertThrows(
        InvalidParameterException.class,
        () -> {
          service.createOrder(new OrderDto(order.getProducts()));
        });
  }

  @Test
  @DisplayName("createOrder Product not in Stock - should return an error")
  void testCreateOrderProductNotInStock() throws URISyntaxException {
    // Arrange: Setup our mock
    Order order = new Order();
    order.getProducts().add(new Product("1", "test", 50, Category.BOOKS));

    when(inventoryClient.checkIfProductExists(any())).thenReturn(true);
    when(inventoryClient.checkIfProductHaveEnoughStock(any(), anyInt())).thenReturn(false);
    doReturn(order).when(repository).save(any());

    Assertions.assertThrows(
        InvalidParameterException.class,
        () -> {
          service.createOrder(new OrderDto(order.getProducts()));
        });
  }

  @Test
  @DisplayName("createOrder Product not in Stock - should return an error")
  void testCreateOrderErrorUpdateStock() throws URISyntaxException {
    // Arrange: Setup our mock
    Order order = new Order();
    order.getProducts().add(new Product("1", "test", 50, Category.BOOKS));

    when(inventoryClient.checkIfProductExists(any())).thenReturn(true);
    when(inventoryClient.checkIfProductHaveEnoughStock(any(), anyInt())).thenReturn(true);
    doThrow(StockUpdateException.class).when(inventoryClient).updateStock(any(), any());

    Assertions.assertThrows(
        StockUpdateException.class,
        () -> {
          service.createOrder(new OrderDto(order.getProducts()));
        });
  }

  @Test
  @DisplayName("updateOrder sucess")
  void testUpdateOrderSuccess() throws URISyntaxException, EmptyOrderException {
    // Arrange: Setup our spy
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Order order = new Order();
    order.getProducts().add(new Product(productName, quantity, Category.BOOKS));
    when(inventoryClient.checkIfProductExists(any())).thenReturn(true);
    when(inventoryClient.checkIfProductHaveEnoughStock(any(), anyInt())).thenReturn(true);
    doReturn(order).when(repository).save(any());

    // Act: Call the services to save a product and then update it
    service.createOrder(new OrderDto(order.getProducts()));
    order.getProducts().add(new Product(productName, quantity, category));
    Order updatedOrder = service.updateOrder(order);

    // Assert: verify the updated product
    Mockito.verify(repository, Mockito.times(1)).save(order);
    Assertions.assertEquals(updatedOrder, order);
  }
}

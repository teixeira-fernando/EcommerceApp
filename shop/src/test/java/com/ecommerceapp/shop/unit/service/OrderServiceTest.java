package com.ecommerceapp.shop.unit.service;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.repository.OrderRepository;
import com.ecommerceapp.shop.service.OrderService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(classes = {OrderService.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class OrderServiceTest {

    /**
     * The service that we want to test.
     */
    @Autowired
    private OrderService service;

    @MockBean
    private OrderRepository repository;

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
        Optional<Order> returnedOrder = service.findById(order.getId());

        // Assert: verify the returned product
        Assertions.assertTrue(returnedOrder.isPresent(), "Order was not found");
        Assertions.assertEquals(returnedOrder.get(), order, "Order should be the same");
    }

    @Test
    @DisplayName("findById not found")
    void testFindByIdNotFound() {
        // Arrange: Setup our mock
        doReturn(Optional.empty()).when(repository).findById("1");

        // Act: Call the service method findById
        Optional<Order> returnedOrder = service.findById("1");

        // Assert the response
        Assertions.assertFalse(returnedOrder.isPresent(), "Order was found, when it shouldn't be");
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
    void testCreateOrderSuccess() {
        // Arrange: Setup our mock
        Order order = new Order();
        doReturn(order).when(repository).save(any());

        // Act: Call the service method create product
        Order createdOrder = service.createOrder(order);

        // Assert: verify the returned product
        Assertions.assertNotNull(createdOrder, "The saved order should not be null");
        Assertions.assertEquals(createdOrder, order, "Order should be the same");
    }

    @Test
    @DisplayName("updateOrder sucess")
    void testUpdateOrderSuccess() {
        // Arrange: Setup our spy
        String productName = "Samsung TV Led";
        Integer quantity = 50;
        Category category = Category.ELECTRONICS;

        Order order = new Order();

        // Act: Call the services to save a product and then update it
        service.createOrder(order);
        order.getProducts().add(new Product(productName, quantity, category));
        service.updateOrder(order);

        // Assert: verify the updated product
        Mockito.verify(repository, Mockito.times(2)).save(order);
    }
}

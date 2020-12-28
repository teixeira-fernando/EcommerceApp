package com.ecommerceapp.shop.unit.controller;

import static com.ecommerceapp.shop.utils.UtilitiesApplication.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecommerceapp.inventory.controller.InventoryController;
import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shop.controller.OrderController;
import com.ecommerceapp.shop.exceptions.EmptyOrderException;
import com.ecommerceapp.shop.exceptions.StockUpdateException;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.model.OrderStatus;
import com.ecommerceapp.shop.service.OrderService;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({OrderController.class, InventoryController.class})
class OrderControllerTest {

  @MockBean private OrderService service;

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /order/{id} - Success")
  void testGetOrderByIdSuccess() throws Exception {
    // Arrange: Setup our mock
    String id = "12345";
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Product mockProduct = new Product(id, productName, quantity, category);
    Order mockOrder = new Order();
    mockOrder.getProducts().add(mockProduct);
    doReturn(mockOrder).when(service).findById(id);

    // Execute the GET request
    mockMvc
        .perform(get("/order/{id}", id))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().string(HttpHeaders.LOCATION, "/order/" + mockOrder.getId()))

        // Validate the returned fields
        .andExpect(jsonPath("$.status", is(OrderStatus.CREATED.toString())))
        .andExpect(jsonPath("$.products[0].id", is(id)))
        .andExpect(jsonPath("$.products[0].name", is(productName)))
        .andExpect(jsonPath("$.products[0].quantity", is(quantity)))
        .andExpect(jsonPath("$.products[0].category", is(category.toString())));
  }

  @Test
  @DisplayName("GET /orders - Success")
  void testGetOrdersSuccess() throws Exception {
    // Arrange: Setup our mock
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);

    Order order1 = new Order();
    Order order2 = new Order();
    order1.getProducts().add(product1);

    doReturn(Arrays.asList(order1, order2)).when(service).findAll();

    // Execute the GET request
    mockMvc
        .perform(get("/orders"))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned array
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  @DisplayName("GET /order/{id} - Not found")
  void testGetOrderByIdNotFound() throws Exception {
    // Arrange: Setup our mock
    doThrow(NoSuchElementException.class).when(service).findById("9999");

    // Execute the GET request
    mockMvc
        .perform(get("/order/{id}", "9999"))

        // Validate the response code
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /order - Success")
  void testCreateOrderSuccess() throws Exception {
    // Arrange: Setup our mock
    String id = "12345";
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    Product mockProduct = new Product(id, productName, quantity, category);
    Order mockOrder = new Order();
    mockOrder.getProducts().add(mockProduct);
    doReturn(mockOrder).when(service).createOrder(any());

    // Execute the POST request
    mockMvc
        .perform(
            post("/order").contentType(MediaType.APPLICATION_JSON).content(asJsonString(mockOrder)))

        // Validate the response code and content type
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().string(HttpHeaders.LOCATION, "/order/" + mockOrder.getId()))

        // Validate the returned fields
        .andExpect(jsonPath("$.status", is(OrderStatus.CREATED.toString())))
        .andExpect(jsonPath("$.products.[0].id", is(id)))
        .andExpect(jsonPath("$.products[0].name", is(productName)))
        .andExpect(jsonPath("$.products[0].quantity", is(quantity)))
        .andExpect(jsonPath("$.products[0].category", is(category.toString())));
  }

  @Test
  @DisplayName("POST /order - Empty Order")
  void testCreateOrderEmptyError() throws Exception {
    // Arrange: Setup our mock
    Order mockOrder = new Order();
    when(service.createOrder(any())).thenThrow(EmptyOrderException.class);

    // Execute the POST request
    mockMvc
        .perform(
            post("/order").contentType(MediaType.APPLICATION_JSON).content(asJsonString(mockOrder)))

        // Validate the response code
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /order - Empty Order")
  void testCreateOrderStockUpdateException() throws Exception {
    // Arrange: Setup our mock
    Order mockOrder = new Order();
    when(service.createOrder(any())).thenThrow(StockUpdateException.class);

    // Execute the POST request
    mockMvc
        .perform(
            post("/order").contentType(MediaType.APPLICATION_JSON).content(asJsonString(mockOrder)))

        // Validate the response code
        .andExpect(status().isServiceUnavailable());
  }

  @Test
  @DisplayName("POST /order - Product not found in the repository")
  void testCreateOrderProductNotFoundError() throws Exception {
    // Arrange: Setup our mock
    String id = "12345";
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    Product mockProduct = new Product(id, productName, quantity, category);
    Order mockOrder = new Order();
    mockOrder.getProducts().add(mockProduct);
    when(service.createOrder(any())).thenThrow(InvalidParameterException.class);

    // Execute the POST request
    mockMvc
        .perform(
            post("/order").contentType(MediaType.APPLICATION_JSON).content(asJsonString(mockOrder)))

        // Validate the response code
        .andExpect(status().isBadRequest());
  }
}

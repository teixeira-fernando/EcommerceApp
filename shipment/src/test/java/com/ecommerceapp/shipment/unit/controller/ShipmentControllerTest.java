package com.ecommerceapp.shipment.unit.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shipment.controller.ShipmentController;
import com.ecommerceapp.shipment.model.Location;
import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.service.ShipmentService;
import com.ecommerceapp.shop.model.Order;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShipmentController.class)
public class ShipmentControllerTest {

  @MockBean private ShipmentService service;

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /shipment/{id} - Success")
  void testGetShipmentByIdSuccess() throws Exception {
    // Arrange: Setup our mock
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;
    Product productToBeInserted = new Product(productName, quantity, category);

    // create an order
    Order order = new Order(List.of(productToBeInserted));

    // create an order shipment
    Location location = new Location("street", 123, "postalcode", "city", "state");
    String shipmentId = "1";
    OrderShipment orderShipment = new OrderShipment("1", order, LocalDate.now(), location);
    doReturn(orderShipment).when(service).findById(shipmentId);

    // Execute the GET request
    mockMvc
        .perform(get("/shipment/{id}", shipmentId))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().string(HttpHeaders.LOCATION, "/shipment/" + shipmentId))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(shipmentId)))
        .andExpect(jsonPath("$.order.status", is("CREATED")))
        .andExpect(jsonPath("$.estimatedDeliveryDate").exists())
        .andExpect(jsonPath("$.deliveryLocation").exists())
        .andExpect(jsonPath("$.createDate").exists())
        .andExpect(jsonPath("$.updateDate").exists())
        .andExpect(jsonPath("$.order.products").isArray());
  }

  @Test
  @DisplayName("GET /shipment/{id} - NOT FOUND")
  void testGetShipmentByIdNotFound() throws Exception {
    // Arrange: Setup our mock
    doThrow(NoSuchElementException.class).when(service).findById("99");

    // Execute the GET request
    mockMvc
        .perform(get("/shipment/{id}", "99"))

        // Validate the response code and content type
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /shipments - Success")
  void testGetAllShipmentsSuccess() throws Exception {
    // Arrange: Setup our mock
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;
    Product productToBeInserted = new Product(productName, quantity, category);

    // create an order
    Order order = new Order(List.of(productToBeInserted));

    // create an order shipment
    Location location = new Location("street", 123, "postalcode", "city", "state");
    String shipmentId = "1";
    OrderShipment orderShipment1 = new OrderShipment("1", order, LocalDate.now(), location);
    OrderShipment orderShipment2 = new OrderShipment("2", order, LocalDate.now(), location);
    doReturn(Arrays.asList(orderShipment1, orderShipment2)).when(service).findAll();

    // Execute the GET request
    mockMvc
        .perform(get("/shipments"))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$.[0].id", is("1")))
        .andExpect(jsonPath("$.[1].id", is("2")));
  }
}

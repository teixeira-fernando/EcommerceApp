package com.ecommerceapp.shipment.unit.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.ecommerceapp.domain.Category;
import com.ecommerceapp.domain.Order;
import com.ecommerceapp.domain.Product;
import com.ecommerceapp.shipment.controller.ShipmentController;
import com.ecommerceapp.shipment.domain.Location;
import com.ecommerceapp.shipment.domain.OrderShipment;
import com.ecommerceapp.shipment.service.ShipmentService;
import java.time.LocalDate;
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
class ShipmentControllerTest {

  @MockBean private ShipmentService service;

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /shipment/{id} - Success")
  void testGetShipmentByIdSuccess() throws Exception {
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
    String orderShipmentID = "1";
    OrderShipment orderShipment =
        new OrderShipment(orderShipmentID, order, LocalDate.now(), location);

    doReturn(orderShipment).when(service).findById(orderShipment.getId());

    // Execute the GET request
    mockMvc
        .perform(get("/shipment/{id}", orderShipmentID))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().string(HttpHeaders.LOCATION, "/shipment/" + orderShipmentID))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(orderShipmentID)))
        .andExpect(jsonPath("$.order", notNullValue()))
        .andExpect(jsonPath("$.deliveryLocation.street", is(location.getStreet())))
        .andExpect(jsonPath("$.deliveryLocation.number", is(location.getNumber())))
        .andExpect(jsonPath("$.deliveryLocation.postalCode", is(location.getPostalCode())))
        .andExpect(jsonPath("$.deliveryLocation.city", is(location.getCity())))
        .andExpect(jsonPath("$.deliveryLocation.state", is(location.getState())))
        .andExpect(jsonPath("$.estimatedDeliveryDate", notNullValue()));
  }

  @Test
  @DisplayName("GET /shipment/{id} - Not Found")
  void testGetShipmentByIdNotFound() throws Exception {
    // Arrange: Setup our mock
    doThrow(NoSuchElementException.class).when(service).findById("9999");

    // Execute the GET request
    mockMvc
        .perform(get("/shipment/{id}", 9999))

        // Validate the response code
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /shipments - Success")
  void testGetShipmentsSuccess() throws Exception {
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
    String orderShipmentID1 = "1";
    String orderShipmentID2 = "2";
    OrderShipment orderShipment1 =
        new OrderShipment(orderShipmentID1, order, LocalDate.now(), location);
    OrderShipment orderShipment2 =
        new OrderShipment(orderShipmentID2, order, LocalDate.now(), location);

    doReturn(List.of(orderShipment1, orderShipment2)).when(service).findAll();

    // Execute the GET request
    mockMvc
        .perform(get("/shipments"))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        /// Validate the returned array
        .andExpect(jsonPath("$", hasSize(2)));
  }
}

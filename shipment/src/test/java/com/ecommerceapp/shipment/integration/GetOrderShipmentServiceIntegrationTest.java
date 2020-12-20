package com.ecommerceapp.shipment.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.unit.repository.MongoDataFile;
import com.ecommerceapp.shipment.unit.repository.MongoSpringExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
public class GetOrderShipmentServiceIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private MongoTemplate mongoTemplate;

  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Test
  @DisplayName("GET /shipment/{id}- Success")
  @MongoDataFile(
      value = "sample.json",
      classType = OrderShipment.class,
      collectionName = "OrderShipment")
  void testGetOrderShipmentByIdSuccess() throws Exception {

    String shipmentId = "5fc97d7b5fd868721138b0ec";

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
        .andExpect(jsonPath("$.deliveryLocation").exists())
        .andExpect(jsonPath("$.order.products").isArray());
  }

  @Test
  @DisplayName("GET /shipments- Success")
  @MongoDataFile(
      value = "sample.json",
      classType = OrderShipment.class,
      collectionName = "OrderShipment")
  void testGetAllOrderShipmentsSuccess() throws Exception {

    String shipmentId = "5fc97d7b5fd868721138b0ec";

    // Execute the GET request
    mockMvc
        .perform(get("/shipments"))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$.[0].id", is(shipmentId)));
  }
}

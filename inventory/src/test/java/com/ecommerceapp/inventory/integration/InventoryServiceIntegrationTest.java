package com.ecommerceapp.inventory.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecommerceapp.domain.Category;
import com.ecommerceapp.domain.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class InventoryServiceIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private MongoTemplate mongoTemplate;

  @BeforeEach
  void cleanup() {
    mongoTemplate.getDb().drop();
  }

  @Test
  @DisplayName("GET /product/{id} - Success")
  void testGetProductByIdSuccess() throws Exception {

    String id = "1";
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    mongoTemplate.insert(new Product(id, productName, quantity, category), "Product");

    // Execute the GET request
    mockMvc
        .perform(get("/product/{id}", 1))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.name", is(productName)))
        .andExpect(jsonPath("$.quantity", is(quantity)))
        .andExpect(jsonPath("$.category", is(category.toString())));
  }

  @Test
  @DisplayName("GET /product/{id} - Not Found")
  void testGetProductByIdNotFound() throws Exception {

    // Execute the GET request
    mockMvc
        .perform(get("/product/{id}", 99))

        // Validate that we get a 404 Not Found response
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /products - Success")
  void testGetProductsSuccess() throws Exception {

    String id = "1";
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    mongoTemplate.insert(new Product(id, productName, quantity, category), "Product");

    // Execute the GET request
    mockMvc
        .perform(get("/products"))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned array
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @DisplayName("POST /product - Success")
  void testCreateProduct() throws Exception {
    // Setup product to create
    String productName = "A New product";
    Integer quantity = 50;
    Category category = Category.VIDEOGAMES;

    Product newProduct = new Product(productName, quantity, category);

    mockMvc
        .perform(
            post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newProduct)))

        // Validate the response code and content type
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().exists(HttpHeaders.LOCATION))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", any(String.class)))
        .andExpect(jsonPath("$.name", is(productName)))
        .andExpect(jsonPath("$.quantity", is(quantity)))
        .andExpect(jsonPath("$.category", is(category.toString())));
  }

  @Test
  @DisplayName("DELETE /product/{id} - Success")
  void testDeleteProductSuccess() throws Exception {

    String id = "1";
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    mongoTemplate.insert(new Product(id, productName, quantity, category), "Product");

    mockMvc
        .perform(delete("/product/{id}", 1).contentType(MediaType.APPLICATION_JSON))

        // Validate the response code and content type
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("DELETE /product/{id} - Not Found")
  void testDeleteProductNotFound() throws Exception {

    mockMvc
        .perform(delete("/product/{id}", 99).contentType(MediaType.APPLICATION_JSON))

        // Validate the response code and content type
        .andExpect(status().isNotFound());
  }
}

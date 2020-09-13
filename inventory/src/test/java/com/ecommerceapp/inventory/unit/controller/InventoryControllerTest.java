package com.ecommerceapp.inventory.unit.controller;

import static com.ecommerceapp.inventory.utils.Utilities.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.service.InventoryService;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

  @MockBean private InventoryService service;

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /product/{id} - Success")
  void testGetProductByIdSuccess() throws Exception {
    // Arrange: Setup our mock
    String id = "12345";
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Product mockProduct = new Product(id, productName, quantity, category);
    doReturn(Optional.of(mockProduct)).when(service).findById(id);

    // Execute the GET request
    mockMvc
        .perform(get("/product/{id}", id))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().string(HttpHeaders.LOCATION, "/product/" + id))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.name", is(productName)))
        .andExpect(jsonPath("$.quantity", is(quantity)))
        .andExpect(jsonPath("$.category", is(category.toString())));
  }

  @Test
  @DisplayName("GET /products - Success")
  void testGetProductsSuccess() throws Exception {
    // Arrange: Setup our mock
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    Product product2 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    doReturn(Arrays.asList(product1, product2)).when(service).findAll();

    // Execute the GET request
    mockMvc
        .perform(get("/products"))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned array
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  @DisplayName("GET /product/{id} - Not found")
  void testGetProductByIdNotFound() throws Exception {
    // Arrange: Setup our mock
    doReturn(Optional.empty()).when(service).findById("9999");

    // Execute the GET request
    mockMvc
        .perform(get("/product/{id}", "9999"))

        // Validate the response code
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /product - Success")
  void testCreateProductSuccess() throws Exception {
    // Arrange: Setup our mock
    String id = "12345";
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    Product mockProduct = new Product(id, productName, quantity, category);
    doReturn(mockProduct).when(service).createProduct(any());

    // Execute the GET request
    mockMvc
        .perform(
            post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(mockProduct)))

        // Validate the response code and content type
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().string(HttpHeaders.LOCATION, "/product/" + id))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.name", is(productName)))
        .andExpect(jsonPath("$.quantity", is(quantity)))
        .andExpect(jsonPath("$.category", is(category.toString())));
  }
}

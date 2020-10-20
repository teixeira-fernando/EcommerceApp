package com.ecommerceapp.inventory.unit.controller;

import com.ecommerceapp.inventory.controller.InventoryController;
import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityExistsException;
import java.util.Arrays;
import java.util.Optional;

import static com.ecommerceapp.inventory.utils.Utilities.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @MockBean
    private InventoryService service;

    @Autowired
    private MockMvc mockMvc;

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

        // Execute the POST request
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

    @Test
    @DisplayName("POST /product - Product already exists")
    void testCreateProductError_ProductAlreadyExists() throws Exception {
        // Arrange: Setup our mock
        String id = "12345";
        String productName = "Dark Souls 3";
        Integer quantity = 20;
        Category category = Category.VIDEOGAMES;

        Product mockProduct = new Product(id, productName, quantity, category);
        when(service.createProduct(any())).thenThrow(EntityExistsException.class);

        // Execute the POST request
        mockMvc
                .perform(
                        post("/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(mockProduct)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /product/{id} - Success")
    void testDeleteProductSuccess() throws Exception {
        // Arrange: Setup our mock
        String id = "12345";
        String productName = "Dark Souls 3";
        Integer quantity = 20;
        Category category = Category.VIDEOGAMES;

        Product mockProduct = new Product(id, productName, quantity, category);
        doReturn(Optional.of(mockProduct)).when(service).findById(any());

        // Execute the DELETE request
        mockMvc
                .perform(
                        delete("/product/{id}", id))

                // Validate the response code
                .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1)).deleteProduct(id);
    }

    @Test
    @DisplayName("DELETE /product/{id} - Not found")
    void testDeleteProductNotFound() throws Exception {
        // Arrange: Setup our mock
        doReturn(Optional.empty()).when(service).findById(any());

        // Execute the DELETE request
        mockMvc
                .perform(
                        delete("/product/{id}", 1))

                // Validate the response code
                .andExpect(status().isNotFound());
    }
}

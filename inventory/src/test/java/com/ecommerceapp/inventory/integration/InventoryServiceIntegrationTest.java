package com.ecommerceapp.inventory.integration;

import static com.ecommerceapp.inventory.utils.Utilities.asJsonString;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.unit.repository.MongoDataFile;
import com.ecommerceapp.inventory.unit.repository.MongoSpringExtension;
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

@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class InventoryServiceIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private MongoTemplate mongoTemplate;

  /**
   * MongoSpringExtension method that returns the autowired MongoTemplate to use for MongoDB
   * interactions.
   *
   * @return The autowired MongoTemplate instance.
   */
  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Test
  @DisplayName("GET /product/{id} - Success")
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testGetProductByIdSuccess() throws Exception {

    // Execute the GET request
    mockMvc
        .perform(get("/product/{id}", 1))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the headers
        .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

        // Validate the returned fields
        .andExpect(jsonPath("$.id", is("1")))
        .andExpect(jsonPath("$.name", is("Samsung TV Led")))
        .andExpect(jsonPath("$.quantity", is(50)))
        .andExpect(jsonPath("$.category", is(Category.ELECTRONICS.toString())));
  }

  @Test
  @DisplayName("GET /product/{id} - Not Found")
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testGetProductByIdNotFound() throws Exception {

    // Execute the GET request
    mockMvc
        .perform(get("/product/{id}", 99))

        // Validate that we get a 404 Not Found response
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /products - Success")
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testGetProductsSuccess() throws Exception {

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
  @DisplayName("POST /product - Success")
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testCreateProduct() throws Exception {
    // Setup product to create
    String productName = "Dark Souls 3";
    Integer quantity = 20;
    Category category = Category.VIDEOGAMES;

    Product newProduct = new Product(productName, quantity, category);

    mockMvc
        .perform(
            post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newProduct)))

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
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testDeleteProductSuccess() throws Exception {
    mockMvc
        .perform(delete("/product/{id}", 1).contentType(MediaType.APPLICATION_JSON))

        // Validate the response code and content type
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("DELETE /product/{id} - Not Found")
  @MongoDataFile(value = "sample.json", classType = Product.class, collectionName = "Products")
  void testDeleteProductNotFound() throws Exception {
    mockMvc
        .perform(delete("/product/{id}", 99).contentType(MediaType.APPLICATION_JSON))

        // Validate the response code and content type
        .andExpect(status().isNotFound());
  }
}

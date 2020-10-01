package com.ecommerceapp.shop.integration;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.model.OrderStatus;
import com.ecommerceapp.shop.unit.repository.MongoDataFile;
import com.ecommerceapp.shop.unit.repository.MongoSpringExtension;
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

import static com.ecommerceapp.shop.utils.Utilities.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrderServiceIntegrationTesting {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

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
    @DisplayName("GET /order/{id} - Success")
    @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
    void testGetOrderByIdSuccess() throws Exception {

        // Execute the GET request
        mockMvc
                .perform(get("/order/{id}", 1))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/order/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.status", is(OrderStatus.CREATED.toString())))
                .andExpect(jsonPath("$.products[0].id", is("1")))
                .andExpect(jsonPath("$.products[0].name", is("Samsung TV Led")))
                .andExpect(jsonPath("$.products[0].quantity", is(50)))
                .andExpect(jsonPath("$.products[0].category", is(Category.ELECTRONICS.toString())));
    }

    @Test
    @DisplayName("GET /order/{id} - Not Found")
    @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
    void testGetOrderByIdNotFound() throws Exception {

        // Execute the GET request
        mockMvc
                .perform(get("/order/{id}", 99))

                // Validate that we get a 404 Not Found response
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders - Success")
    @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
    void testGetOrdersSuccess() throws Exception {

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
    @DisplayName("POST /order - Success")
    @MongoDataFile(value = "sample.json", classType = Order.class, collectionName = "Order")
    void testCreateOrder() throws Exception {
        // Setup product to create
        String productName = "Dark Souls 3";
        Integer quantity = 20;
        Category category = Category.VIDEOGAMES;

        Product newProduct = new Product(productName, quantity, category);
        Order order = new Order();
        order.getProducts().add(newProduct);

        mockMvc
                .perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(order)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().exists(HttpHeaders.LOCATION))

                // Validate the returned fields
                .andExpect(jsonPath("$.status", is(OrderStatus.CREATED.toString())))
                .andExpect(jsonPath("$.products[0].name", is(productName)))
                .andExpect(jsonPath("$.products[0].quantity", is(quantity)))
                .andExpect(jsonPath("$.products[0].category", is(category.toString())));
    }
}


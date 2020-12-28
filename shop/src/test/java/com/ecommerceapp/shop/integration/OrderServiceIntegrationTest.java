package com.ecommerceapp.shop.integration;

import static com.ecommerceapp.shop.utils.UtilitiesApplication.asJsonString;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.model.OrderStatus;
import com.ecommerceapp.shop.utils.UtilitiesApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.ArrayList;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EmbeddedKafka(ports = 63178)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceIntegrationTest {

  final EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1);
  @Autowired private MockMvc mockMvc;
  @Autowired private MongoTemplate mongoTemplate;
  private WireMockServer wireMockServer;

  @BeforeAll
  public void setup() {
    embeddedKafkaBroker.setZkPort(63178);
    embeddedKafkaBroker.kafkaPorts(63178);
  }

  @BeforeEach
  void configureSystemUnderTest() {
    int port = Integer.parseInt(UtilitiesApplication.readPropertyValue("inventory.port"));
    this.wireMockServer = new WireMockServer(options().port(port));
    this.wireMockServer.start();
    configureFor("localhost", this.wireMockServer.port());
    mongoTemplate.getDb().drop();
  }

  @AfterEach
  void stopWireMockServer() {
    this.wireMockServer.stop();
  }

  @Test
  @DisplayName("GET /order/{id} - Success")
  void testGetOrderByIdSuccess() throws Exception {

    String id = "123";
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Order order = new Order("1", new ArrayList<>());
    order.getProducts().add(new Product(id, productName, quantity, category));
    mongoTemplate.insert(order, "Order");

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
        .andExpect(jsonPath("$.products[0].id", is(id)))
        .andExpect(jsonPath("$.products[0].name", is(productName)))
        .andExpect(jsonPath("$.products[0].quantity", is(quantity)))
        .andExpect(jsonPath("$.products[0].category", is(category.toString())));
  }

  @DisplayName("GET /order/{id} - Not Found")
  void testGetOrderByIdNotFound() throws Exception {

    // Execute the GET request
    mockMvc
        .perform(get("/order/{id}", 99))

        // Validate that we get a 404 Not Found response
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /orders - Success")
  void testGetOrdersSuccess() throws Exception {

    String id = "1";
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Order order = new Order(new ArrayList<>());
    order.getProducts().add(new Product(productName, quantity, category));
    mongoTemplate.insert(order, "Order");

    // Execute the GET request
    mockMvc
        .perform(get("/orders"))

        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned array
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @DisplayName("POST /order - Success")
  void testCreateOrder() throws Exception {
    // Setup product to create
    String id = "1";
    String productName = "Samsung TV Led";
    Integer quantity = 10;
    Category category = Category.ELECTRONICS;

    Product newProduct = new Product(id, productName, quantity, category);
    Order order = new Order();
    order.getProducts().add(newProduct);

    wireMockServer.stubFor(
        WireMock.get(WireMock.urlEqualTo("/product/" + id))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(UtilitiesApplication.asJsonString(newProduct))));

    wireMockServer.stubFor(
        WireMock.post(WireMock.urlEqualTo("/product/" + id + "/changeStock"))
            .willReturn(aResponse().withStatus(200)));

    mockMvc
        .perform(
            post("/order").contentType(MediaType.APPLICATION_JSON).content(asJsonString(order)))

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

package com.ecommerceapp.shipment.integration;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shipment.service.kafka.MessageListener;
import com.ecommerceapp.shop.model.Order;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(ports = 9092)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({KafkaProducerTestConfiguration.class, MessageListener.class})
@AutoConfigureMockMvc
public class OrderShipmentServiceIntegrationTest {

  private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired private KafkaTemplate<String, Order> orderKafkaTemplate;

  @Value("${order.topic.name}")
  private String topicName;

  @Autowired private MockMvc mockMvc;

  @Autowired private MongoTemplate mongoTemplate;

  @BeforeAll
  public void setup() {
    embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, 1, topicName);
    embeddedKafkaBroker.setZkPort(9092);
    embeddedKafkaBroker.kafkaPorts(9092);
  }

  @BeforeEach
  public void resetMongoDB() {
    mongoTemplate.getDb().drop();
  }

  @Test
  @DisplayName("Create Order Shipment reading message from Kafka - Success")
  void testCreateOrderShipment() throws Exception {
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    ArrayList<Product> products = new ArrayList<>();
    products.add(product1);
    Order order1 = new Order("123", products);

    orderKafkaTemplate.send(topicName, order1);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                mockMvc
                    .perform(get("/shipments"))

                    // Validate the response code and content type
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    // Make sure that the new OrderPayment was inserted
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].order.id", equalTo("123"))));
  }

  @Test
  @DisplayName("Sending multiple orders through Kafka - Success")
  void testCreateMultipleOrderShipment() throws Exception {
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    ArrayList<Product> products = new ArrayList<>();
    products.add(product1);
    Order order1 = new Order("1", products);
    Order order2 = new Order("2", products);
    Order order3 = new Order("3", products);
    Order order4 = new Order("4", products);
    Order order5 = new Order("5", products);

    orderKafkaTemplate.send(topicName, order1);
    orderKafkaTemplate.send(topicName, order2);
    orderKafkaTemplate.send(topicName, order3);
    orderKafkaTemplate.send(topicName, order4);
    orderKafkaTemplate.send(topicName, order5);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                mockMvc
                    .perform(get("/shipments"))

                    // Validate the response code and content type
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    // Make sure that the new OrderPayment was inserted
                    .andExpect(jsonPath("$", hasSize(5)))
                    .andExpect(jsonPath("$[0].order.id", equalTo("1"))));
  }
}

package com.ecommerceapp.shipment.integration;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.unit.repository.MongoDataFile;
import com.ecommerceapp.shipment.unit.repository.MongoSpringExtension;
import com.ecommerceapp.shop.model.Order;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@SpringBootTest
@EmbeddedKafka(ports = 9095)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({KafkaProducerTestConfiguration.class})
@AutoConfigureMockMvc
public class OrderShipmentServiceIntegrationTest {

  private EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1);

  @Autowired private KafkaTemplate<String, Order> orderKafkaTemplate;

  @Value("${order.topic.name}")
  private String topicName;

  @Autowired private MockMvc mockMvc;

  @Autowired private MongoTemplate mongoTemplate;

  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @BeforeAll
  public void setup() {
    embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, 1, topicName);
    embeddedKafkaBroker.setZkPort(63179);
    embeddedKafkaBroker.kafkaPorts(9095);
  }

  @Test
  @DisplayName("Create Order Shipment reading message from Kafka - Success")
  @MongoDataFile(
      value = "sample.json",
      classType = OrderShipment.class,
      collectionName = "OrderShipment")
  void testCreateOrderShipment() throws Exception {
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    ArrayList<Product> products = new ArrayList<>();
    products.add(product1);
    Order order1 = new Order("123", products);

    orderKafkaTemplate.send(topicName, order1);

    await()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                Assertions.assertEquals(
                    2, mongoTemplate.findAll(OrderShipment.class, "OrderShipment").size()));
  }

  @Test
  @DisplayName("Sending multiple orders through Kafka - Success")
  @MongoDataFile(
      value = "sample.json",
      classType = OrderShipment.class,
      collectionName = "OrderShipment")
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
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                Assertions.assertEquals(
                    6, mongoTemplate.findAll(OrderShipment.class, "OrderShipment").size()));
  }
}

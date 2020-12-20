package com.ecommerceapp.shipment.integration;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.service.kafka.KafkaConsumerConfig;
import com.ecommerceapp.shipment.service.kafka.MessageListenerShipment;
import com.ecommerceapp.shop.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@EmbeddedKafka(ports = 9092, partitions = 1)
@DirtiesContext
@Import({
  KafkaProducerTestConfiguration.class,
  MessageListenerShipment.class,
  KafkaConsumerConfig.class
})
public class CreateOrderShipmentServiceIntegrationTest {

  @Autowired private KafkaTemplate<String, Order> orderKafkaTemplate;

  @Value("${order.topic.name}")
  private String topicName;

  @Autowired private MongoTemplate mongoTemplate;

  @AfterEach
  void cleanUp() {
    mongoTemplate.getDb().drop();
  }

  @Test
  @DisplayName("Create Order Shipment reading message from Kafka - Success")
  void testCreateOrderShipment() {
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    ArrayList<Product> products = new ArrayList<>();
    products.add(product1);
    Order order1 = new Order("123", products);

    orderKafkaTemplate.send(topicName, order1);

    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(() -> mongoTemplate.findAll(OrderShipment.class, "OrderShipment").size() == 1);
  }

  @Test
  @DisplayName("Sending multiple orders through Kafka - Success")
  void testCreateMultipleOrderShipment() {
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
        .until(() -> mongoTemplate.findAll(OrderShipment.class, "OrderShipment").size() == 5);
  }
}

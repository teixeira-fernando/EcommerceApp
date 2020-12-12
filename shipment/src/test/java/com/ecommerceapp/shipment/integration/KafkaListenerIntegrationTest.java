package com.ecommerceapp.shipment.integration;

import static org.awaitility.Awaitility.await;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shipment.repository.OrderShipmentRepository;
import com.ecommerceapp.shipment.service.ShipmentService;
import com.ecommerceapp.shipment.service.kafka.KafkaConsumerConfig;
import com.ecommerceapp.shipment.service.kafka.MessageListenerShipment;
import com.ecommerceapp.shop.model.Order;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(
    classes = {
      MessageListenerShipment.class,
      OrderShipmentRepository.class,
      ShipmentService.class,
      MessageListenerShipment.class,
      KafkaProducerTestConfiguration.class,
      KafkaConsumerConfig.class
    })
@EmbeddedKafka(ports = 9095, partitions = 1)
@Import({
  KafkaProducerTestConfiguration.class,
  MessageListenerShipment.class,
  KafkaConsumerConfig.class
})
@ContextConfiguration(classes = {KafkaConsumerConfig.class, KafkaProducerTestConfiguration.class})
public class KafkaListenerIntegrationTest {

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired private KafkaTemplate<String, Order> orderKafkaTemplate;

  @MockBean private OrderShipmentRepository repository;

  @Autowired private ShipmentService shipmentService;

  @Autowired private MessageListenerShipment messageListenerShipment;

  @Autowired private KafkaProducerTestConfiguration kafkaProducerTestConfiguration;

  @Value("${order.topic.name}")
  private String topicName;

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
        .untilAsserted(() -> Mockito.verify(repository, Mockito.times(1)).save(Mockito.any()));
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
        .untilAsserted(() -> Mockito.verify(repository, Mockito.times(5)).save(Mockito.any()));
  }
}

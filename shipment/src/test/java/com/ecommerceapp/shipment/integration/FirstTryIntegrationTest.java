package com.ecommerceapp.shipment.integration;

import static org.awaitility.Awaitility.await;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shop.model.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@EmbeddedKafka(ports = 9095)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({KafkaProducerTestConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("default")
public class FirstTryIntegrationTest {

  private static final String TOPIC = "shipment";

  @Autowired private KafkaTemplate<String, Order> orderKafkaTemplate;

  final EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1);

  BlockingQueue<ConsumerRecord<String, Order>> records;

  KafkaMessageListenerContainer<String, Order> container;

  @Autowired private MockMvc mockMvc;

  @Autowired private MongoTemplate mongoTemplate;

  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @BeforeAll
  public void setUp() {
    embeddedKafkaBroker.setZkPort(63179);
    embeddedKafkaBroker.kafkaPorts(9095);
    Map<String, Object> configs =
        new HashMap<>(KafkaTestUtils.consumerProps("group_id", "true", embeddedKafkaBroker));
    DefaultKafkaConsumerFactory<String, Order> consumerFactory =
        new DefaultKafkaConsumerFactory<>(
            configs, new StringDeserializer(), new JsonDeserializer<>(Order.class));
    ContainerProperties containerProperties = new ContainerProperties(TOPIC);
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    records = new LinkedBlockingDeque<>();
    container.setupMessageListener((MessageListener<String, Order>) records::add);
    container.start();
    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @Test
  void testCreateOrderShipment() throws Exception {
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    ArrayList<Product> products = new ArrayList<>();
    products.add(product1);
    Order order1 = new Order("123", products);

    orderKafkaTemplate.send(TOPIC, UUID.randomUUID().toString(), order1);

    await()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                Assertions.assertEquals(
                    1, mongoTemplate.findAll(OrderShipment.class, "OrderShipment").size()));
  }
}

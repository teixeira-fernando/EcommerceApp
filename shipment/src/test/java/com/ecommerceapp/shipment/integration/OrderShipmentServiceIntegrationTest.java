package com.ecommerceapp.shipment.integration;

/*

@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
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
    // embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, 1, topicName);
    embeddedKafkaBroker.setZkPort(63178);
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
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                mockMvc
                    .perform(get("/shipments"))

                    // Validate the response code and content type
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    // Make sure that the new OrderPayment was inserted
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[1].order.id", equalTo("123"))));
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
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                mockMvc
                    .perform(get("/shipments"))

                    // Validate the response code and content type
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    // Make sure that the new OrderPayment was inserted
                    .andExpect(jsonPath("$", hasSize(6)))
                    .andExpect(jsonPath("$[1].order.id", equalTo("1"))));
  }
}
*/

package com.ecommerceapp.shipment.contract;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shop.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(
    providerName = "OrderModule",
    providerType = ProviderType.ASYNCH,
    pactVersion = PactSpecVersion.V3)
class CreateShipmentConsumerPact {

  static ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
  }

  @BeforeAll
  public static void config() {
    // System.setProperty("pact.writer.overwrite", "true");
  }

  @BeforeEach
  void setup() {
    System.setProperty("pact.verifier.publishResults", "true");
  }

  @Pact(provider = "OrderModule", consumer = "ShipmentModule")
  public MessagePact createPactJsonMessage(MessagePactBuilder builder) {
    String id = "1";
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Order order = new Order("1", new ArrayList<>());
    order.getProducts().add(new Product(id, productName, quantity, category));

    PactDslJsonBody body = new PactDslJsonBody();
    body.stringType("id", "1");
    body.eachLike("products")
        .stringType("id", "1")
        .stringType("name", "Samsung TV")
        .integerType("quantity", 50)
        .stringType("category", Category.ELECTRONICS.toString())
        .closeArray();
    body.minMaxArrayLike("products", 0, 1);

    Map<String, String> metadata = new HashMap<String, String>();
    metadata.put("Content-Type", "application/json");

    return builder
        .given("Send an order to Shipment Module")
        .expectsToReceive("Order in Json format")
        .withMetadata(metadata)
        .withContent(body)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "createPactJsonMessage")
  void testCreatePactJsonMessage(List<Message> messages) {
    try {
      Order order = mapper.readValue(messages.get(0).getContents().valueAsString(), Order.class);

      Assertions.assertEquals("1", order.getId());
      Assertions.assertNotNull(order.getProducts());

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}

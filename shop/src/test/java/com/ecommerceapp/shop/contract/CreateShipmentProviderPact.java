package com.ecommerceapp.shop.contract;

import au.com.dius.pact.core.model.Interaction;
import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.AmpqTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.*;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shop.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Provider("ShopModule")
@PactBroker(
    scheme = "https",
    host = "${PACT_BROKER_HOST}",
    consumerVersionSelectors = {@VersionSelector(latest = "true")},
    authentication = @PactBrokerAuth(token = "${PACT_BROKER_TOKEN}"))
public class CreateShipmentProviderPact {

  static ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
  }

  @BeforeEach
  void before(PactVerificationContext context) {
    System.setProperty("pact.verifier.publishResults", "true");
    context.setTarget(new AmpqTestTarget());
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTestTemplate(
      Pact pact, Interaction interaction, PactVerificationContext context) {
    context.verifyInteraction();
  }

  @State("Send an order to Shipment Module")
  void sendOrderState() {}

  @PactVerifyProvider("Order in Json format")
  public String verifyMessageForOrderJson() {

    try {
      String id = "1";
      String productName = "Samsung TV Led";
      Integer quantity = 50;
      Category category = Category.ELECTRONICS;

      Order order = new Order("1", new ArrayList<>());
      order.getProducts().add(new Product(id, productName, quantity, category));

      String body = mapper.writeValueAsString(order);

      return body;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}

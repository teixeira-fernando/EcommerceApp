package com.ecommerceapp.shop.contract;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit5.*;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import com.ecommerceapp.shop.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Provider("OrderModule")
@PactBroker(host = "localhost", port = "9292")
@VerificationReports
@IgnoreNoPactsToVerify
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
  void pactVerificationTestTemplate(PactVerificationContext context) {
    context.setTarget(new AmpqTestTarget());
    context.verifyInteraction();
  }

  @State("Send an order to Shipment Module")
  void sendOrderState() {}

  @PactVerifyProvider("Order in Json format")
  public String verifyMessageForOrderJson() {

    try {
      String body = mapper.writeValueAsString(new Order("1", new ArrayList<>()));

      return body;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}

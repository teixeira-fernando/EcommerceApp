package com.ecommerceapp.shipment.contract;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.utils.UtilitiesApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "OrderModule", providerType = ProviderType.ASYNCH)
public class CreateShipmentConsumerPact {

    static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    @BeforeAll
    public static void config() {
        System.setProperty("pact.writer.overwrite", "true");
    }

    @BeforeEach
    void setup() {
        System.setProperty("pact.verifier.publishResults", "true");
    }

    @Pact(provider = "OrderModule", consumer = "ShipmentModule")
    public MessagePact createPactJsonMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();
        body.stringType("id", "1");

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
        try{
            Order order = mapper.readValue(messages.get(0).getContents().valueAsString(), Order.class);

            Assertions.assertEquals(order.getId(), "1");
            Assertions.assertEquals(order.getProducts().size(), 0);

        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }
}

package com.ecommerceapp.shop.contract;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "InventoryModule", pactVersion = PactSpecVersion.V3)
class UpdateStockConsumerPact {

  @BeforeAll
  public static void config() {
    /*System.setProperty("pact.writer.overwrite", "true");*/
  }

  @BeforeEach
  public void setUp(MockServer mockServer) {
    assertThat(mockServer, is(notNullValue()));
  }

  @Pact(provider = "InventoryModule", consumer = "ShopModule")
  public RequestResponsePact updateStock(PactDslWithProvider builder) {
    return builder
        .given("trying to update the stock of a product by id")
        .uponReceiving("Update a product stock from inventory module")
        .path("/product/1/changeStock")
        .method("POST")
        .body("{\n" + "  \"operation\": \"INCREMENT\",\n" + "  \"quantity\": 30\n" + "}")
        .willRespondWith()
        .status(200)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "updateStock")
  void testUpdateStock(MockServer mockServer)
      throws IOException, URISyntaxException, InterruptedException {
    HttpResponse httpResponse =
        Request.Post(mockServer.getUrl() + "/product/1/changeStock")
            .bodyString(
                "{\n" + "  \"operation\": \"INCREMENT\",\n" + "  \"quantity\": 30\n" + "}",
                ContentType.APPLICATION_JSON)
            .execute()
            .returnResponse();
    Assertions.assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
  }
}

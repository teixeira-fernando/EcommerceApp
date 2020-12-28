package com.ecommerceapp.shop.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "ProductModule")
class UpdateStockConsumerPact {

  private HttpClient client;

  public UpdateStockConsumerPact() {
    client =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .version(HttpClient.Version.HTTP_2)
            .build();
  }

  @BeforeAll
  public static void config() {
    /*System.setProperty("pact.writer.overwrite", "true");*/
  }

  @BeforeEach
  public void setUp(MockServer mockServer) {
    assertThat(mockServer, is(notNullValue()));
  }

  @Pact(provider = "ProductModule", consumer = "OrderModule")
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
    Assertions.assertEquals( HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
  }
}

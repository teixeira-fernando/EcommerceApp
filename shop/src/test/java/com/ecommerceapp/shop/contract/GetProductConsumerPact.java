package com.ecommerceapp.shop.contract;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.ecommerceapp.shop.model.inventory.Category;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "InventoryModule", pactVersion = PactSpecVersion.V3)
class GetProductConsumerPact {

  @BeforeAll
  public static void config() {
    /*System.setProperty("pact.writer.overwrite", "true");*/
  }

  @BeforeEach
  public void setUp(MockServer mockServer) {
    assertThat(mockServer, is(notNullValue()));
  }

  @Pact(provider = "InventoryModule", consumer = "ShopModule")
  public RequestResponsePact getProduct(PactDslWithProvider builder) {
    return builder
        .given("trying to get a product by id")
        .uponReceiving("Get a product from inventory module")
        .path("/product/1")
        .method("GET")
        .willRespondWith()
        .status(200)
        .body(
            new PactDslJsonBody()
                .stringValue("id", "1")
                .stringType("name")
                .integerType("quantity")
                .stringType("category", Category.ELECTRONICS.toString()))
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "getProduct")
  void testGetProduct(MockServer mockServer) throws IOException {
    HttpResponse httpResponse =
        Request.Get(mockServer.getUrl() + "/product/1").execute().returnResponse();
    assertThat(httpResponse.getStatusLine().getStatusCode(), is(equalTo(200)));
  }
}

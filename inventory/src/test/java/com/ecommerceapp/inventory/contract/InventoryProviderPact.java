package com.ecommerceapp.inventory.contract;

import static org.mockito.BDDMockito.given;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import com.ecommerceapp.inventory.dto.request.ChangeStockDto;
import com.ecommerceapp.inventory.dto.request.StockOperation;
import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.service.InventoryService;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("InventoryModule")
@PactBroker(
    url = "${PACT_BROKER_URL}",
    consumerVersionSelectors = {
      @VersionSelector(latest = "true")
    },
    authentication = @PactBrokerAuth(token = "${PACT_BROKER_TOKEN}"))
@VerificationReports
@IgnoreNoPactsToVerify
class InventoryProviderPact {

  @LocalServerPort private int localServerPort;

  @MockBean private InventoryService inventoryService;

  @BeforeEach
  void setUp(PactVerificationContext context) throws MalformedURLException {
    context.setTarget(HttpTestTarget.fromUrl(new URL("http://localhost:" + localServerPort)));
  }

  @TestTemplate
  @ExtendWith(PactVerificationSpringProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context) {
    System.setProperty("pact.verifier.publishResults", "true");
    context.verifyInteraction();
  }

  @State("trying to get a product by id")
  public void getProduct() {
    given(inventoryService.findById("1"))
        .willReturn(new Product("1", "Samsung TV", 50, Category.ELECTRONICS));
  }

  @State("trying to update the stock of a product by id")
  public void updateStock() {
    String id = "1";
    String name = "Samsung TV";
    int quantity = 50;
    Category category = Category.ELECTRONICS;
    int stockUpdateAmount = 30;
    Product product = new Product(id, name, quantity, category);
    given(inventoryService.findById("1")).willReturn(product);
    given(
            inventoryService.updateStock(
                product, new ChangeStockDto(stockUpdateAmount, StockOperation.INCREMENT)))
        .willReturn(quantity + stockUpdateAmount);
  }
}

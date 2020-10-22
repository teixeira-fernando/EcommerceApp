package com.ecommerceapp.shop.service;

import static java.time.temporal.ChronoUnit.SECONDS;

import com.ecommerceapp.shop.dto.request.ChangeStockDto;
import com.ecommerceapp.shop.exceptions.StockUpdateException;
import com.ecommerceapp.shop.utils.Utilities;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InventoryClient {

  private static final Logger logger = LogManager.getLogger(InventoryClient.class);

  private HttpClient client;

  private String INVENTORYHOST;
  private long TIMEOUT = 10;

  public InventoryClient() {
    client =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .version(HttpClient.Version.HTTP_2)
            .build();
    // get the property value
    INVENTORYHOST = readPropertyValue("inventory.host");
  }

  private HttpClient getClient() {
    return client;
  }

  private HttpRequest searchForProductRequest(String id) throws URISyntaxException {

    return HttpRequest.newBuilder().uri(new URI(INVENTORYHOST + "/product/" + id)).GET().build();
  }

  private HttpRequest changeStockRequest(String id, ChangeStockDto stockOperation)
      throws URISyntaxException {

    return HttpRequest.newBuilder()
        .uri(new URI(INVENTORYHOST + "/product/" + id + "/changeStock"))
        .timeout(Duration.of(TIMEOUT, SECONDS))
        .POST(HttpRequest.BodyPublishers.ofString(Utilities.asJsonString(stockOperation)))
        .build();
  }

  private String readPropertyValue(String propertyPath) {
    Properties prop = new Properties();

    try (InputStream input =
        InventoryClient.class.getClassLoader().getResourceAsStream("application.properties")) {

      if (input == null) {
        throw new FileNotFoundException(
            "unable to find application.properties to read the property value");
      }
      // load a properties file from class path, inside static method
      prop.load(input);

    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return prop.getProperty(propertyPath);
  }

  private HttpResponse<String> executeRequest(HttpRequest request) {
    HttpResponse<String> response = null;
    try {
      response = this.getClient().send(request, HttpResponse.BodyHandlers.ofString());

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return response;
  }

  public boolean checkIfProductExists(String id) throws URISyntaxException {
    return this.executeRequest(this.searchForProductRequest(id)).statusCode()
        == HttpStatus.OK.value();
  }

  public boolean checkIfProductHaveEnoughStock(String id, int quantity) throws URISyntaxException {
    HttpResponse<String> response = this.executeRequest(this.searchForProductRequest(id));
    JSONObject jsonObject = new JSONObject(response.body());
    int currentStock = (int) jsonObject.get("quantity");
    logger.debug("Value of quantity: " + currentStock);
    logger.debug("My response body: " + response.body());
    if (quantity > currentStock) {
      return false;
    } else {
      return true;
    }
  }

  public void changeStock(String id, ChangeStockDto stockOperation)
      throws URISyntaxException, StockUpdateException {
    HttpResponse<String> response =
        this.executeRequest(this.changeStockRequest(id, stockOperation));
    if (response.statusCode() != HttpStatus.OK.value()) {
      throw new StockUpdateException();
    }
  }
}

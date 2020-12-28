package com.ecommerceapp.shop.service;

import com.ecommerceapp.shop.dto.request.ChangeStockDto;
import com.ecommerceapp.shop.exceptions.StockUpdateException;
import com.ecommerceapp.shop.utils.UtilitiesApplication;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

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
    INVENTORYHOST = UtilitiesApplication.readPropertyValue("inventory.host");
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
        .setHeader("Content-Type", "application/json")
        .POST(
            HttpRequest.BodyPublishers.ofString(UtilitiesApplication.asJsonString(stockOperation)))
        .build();
  }

  private HttpRequest getAllProductsRequest() throws URISyntaxException {

    return HttpRequest.newBuilder()
        .uri(new URI(INVENTORYHOST + "/products"))
        .timeout(Duration.of(TIMEOUT, SECONDS))
        .GET()
        .build();
  }

  private HttpResponse<String> executeRequest(HttpRequest request){
    HttpResponse<String> response = null;
    try {
      response = this.getClient().send(request, HttpResponse.BodyHandlers.ofString());

    } catch (IOException e) {
      logger.error(e.getStackTrace());
    } catch (InterruptedException e) {
      logger.log(Level.WARN, "Interrupted!", e);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
    }
    if(response == null){
      throw new NullPointerException("Something went wrong when trying to communicate with the other module");
    }
    return response;
  }

  public boolean checkIfProductExists(String id) throws URISyntaxException {
    return this.executeRequest(this.searchForProductRequest(id)).statusCode()
        == HttpStatus.OK.value();
  }

  public boolean checkIfProductHaveEnoughStock(String id, int desiredQuantity)
      throws URISyntaxException {
    HttpResponse<String> response = this.executeRequest(this.searchForProductRequest(id));
    JSONObject jsonObject = new JSONObject(response.body());
    int currentStock = (int) jsonObject.get("quantity");
    logger.debug("Value of quantity: " + currentStock);
    logger.debug("My response body: " + response.body());
    if (desiredQuantity > currentStock) {
      return false;
    } else {
      return true;
    }
  }

  public void updateStock(String id, ChangeStockDto stockOperation) throws URISyntaxException {
    HttpResponse<String> response =
        this.executeRequest(this.changeStockRequest(id, stockOperation));
    if (response.statusCode() != HttpStatus.OK.value()) {
      throw new StockUpdateException("Something went wrong when trying to update the stock");
    }
  }

  public JSONArray getAllProducts() throws URISyntaxException {
    HttpResponse<String> response = this.executeRequest(this.getAllProductsRequest());
    JSONArray jsonArray = new JSONArray(response.body());
    return jsonArray;
  }
}

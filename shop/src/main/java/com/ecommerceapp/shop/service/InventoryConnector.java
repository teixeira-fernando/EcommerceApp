package com.ecommerceapp.shop.service;

import com.ecommerceapp.inventory.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Properties;

import static java.time.temporal.ChronoUnit.SECONDS;

public class InventoryConnector {

    private static final Logger logger = LogManager.getLogger(InventoryConnector.class);

    private HttpClient client;

    private String INVENTORYHOST;
    private long TIMEOUT = 10;

    public InventoryConnector() {
        client = HttpClient.newHttpClient();
        //get the property value
        INVENTORYHOST = readPropertyValue("inventory.host");
    }

    public HttpClient getClient() {
        return client;
    }

    public HttpRequest searchForProduct(String id) throws URISyntaxException {

        logger.info("Value of host: " + INVENTORYHOST);

        return HttpRequest.newBuilder()
                .uri(new URI(INVENTORYHOST + "/product/" + id))
                .timeout(Duration.of(TIMEOUT, SECONDS))
                .GET()
                .build();
    }

    public String readPropertyValue(String propertyPath){
        Properties prop = new Properties();

        try (InputStream input = InventoryConnector.class.getClassLoader().getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new FileNotFoundException("unable to find application.properties to read the property value");
            }
            //load a properties file from class path, inside static method
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop.getProperty(propertyPath);
    }
}

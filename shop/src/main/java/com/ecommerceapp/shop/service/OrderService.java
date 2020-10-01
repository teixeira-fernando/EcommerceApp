package com.ecommerceapp.shop.service;

import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import com.ecommerceapp.shop.controller.OrderController;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    private OrderRepository repository;

    private InventoryConnector inventoryConnector;

    public OrderService(OrderRepository repository) {

        this.repository = repository;
        inventoryConnector = new InventoryConnector();
    }

    public Optional<Order> findById(String id) {
        return this.repository.findById(id);
    }

    public List<Order> findAll() {
        return this.repository.findAll();
    }

    public Order createOrder(Order order) {
        HttpResponse<String> response = null;
        try {
            response = inventoryConnector.getClient().send(inventoryConnector.searchForProduct(order.getProducts().get(0).getId()), HttpResponse.BodyHandlers.ofString());

            logger.info("Search for product - Status Code: ", response.statusCode());
            logger.info("Search for product - Response Body: ", response.body());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(response.statusCode() != HttpStatus.OK.value()){
            throw new InvalidParameterException("Failed to search for the product in the inventory");
        }

        return this.repository.save(order);
    }

    public Order updateOrder(Order order) {
        return this.repository.save(order);
    }

}

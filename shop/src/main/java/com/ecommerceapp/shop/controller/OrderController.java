package com.ecommerceapp.shop.controller;

import com.ecommerceapp.inventory.controller.InventoryController;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.service.InventoryService;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Returns the product with the specified ID.
     *
     * @param id The ID of the order to retrieve.
     * @return The order with the specified ID.
     */
    @GetMapping("/order/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {

        return orderService
                .findById(id)
                .map(
                        order -> {
                            try {
                                return ResponseEntity.ok()
                                        .location(new URI("/order/" + order.getId()))
                                        .body(order);
                            } catch (URISyntaxException e) {
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                            }
                        })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns all orders in the database.
     *
     * @return All orders in the database.
     */
    @GetMapping("/orders")
    public Iterable<Order> getOrders() {
        return orderService.findAll();
    }

    /**
     * Creates a new order.
     *
     * @param order The order to create.
     * @return The created order.
     */
    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        logger.info(
                "Creating new order");

        // Create the new order
        Order newOrder = orderService.createOrder(order);

        try {
            // Build a created response
            return ResponseEntity.created(new URI("/order/" + newOrder.getId())).body(newOrder);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

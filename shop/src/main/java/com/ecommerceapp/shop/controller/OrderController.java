package com.ecommerceapp.shop.controller;

import com.ecommerceapp.shop.exceptions.EmptyOrderException;
import com.ecommerceapp.shop.exceptions.StockUpdateException;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.service.OrderService;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

  private static final Logger logger = LogManager.getLogger(OrderController.class);

  @Autowired private OrderService orderService;

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
                return ResponseEntity.ok().location(new URI("/order/" + order.getId())).body(order);
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
    logger.info("Creating new order");

    try {
      // Create the new order
      Order newOrder = orderService.createOrder(order);

      // Build a created response
      return ResponseEntity.created(new URI("/order/" + newOrder.getId())).body(newOrder);
    } catch (InvalidParameterException e) {
      return new ResponseEntity(
          "A product included in the Order was not found in the inventory", HttpStatus.BAD_REQUEST);
    } catch (EmptyOrderException e) {
      return new ResponseEntity("The order does not contain any product", HttpStatus.BAD_REQUEST);
    } catch (StockUpdateException e) {
      return new ResponseEntity("Something went wrong when trying to communicate with the inventory service", HttpStatus.SERVICE_UNAVAILABLE);
    } catch (URISyntaxException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

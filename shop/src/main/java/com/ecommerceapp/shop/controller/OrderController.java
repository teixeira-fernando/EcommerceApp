package com.ecommerceapp.shop.controller;

import com.ecommerceapp.domain.Order;
import com.ecommerceapp.shop.dto.request.OrderDto;
import com.ecommerceapp.shop.exceptions.EmptyOrderException;
import com.ecommerceapp.shop.exceptions.StockUpdateException;
import com.ecommerceapp.shop.service.OrderService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Expected order to a valid request"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  @GetMapping("/order/{id}")
  public ResponseEntity<Order> getOrder(@PathVariable String id) {

    try {
      Order order = orderService.findById(id);

      return ResponseEntity.ok().location(new URI("/order/" + order.getId())).body(order);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    } catch (URISyntaxException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Returns all orders in the database.
   *
   * @return All orders in the database.
   */
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "List of Orders"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  @GetMapping("/orders")
  public List<Order> getOrders() {
    return orderService.findAll();
  }

  /**
   * Creates a new order.
   *
   * @param order The order to create.
   * @return The created order.
   */
  @PostMapping("/order")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Expected order to a valid request"),
        @ApiResponse(
            responseCode = "400",
            description = "There is a problem with the request parameters"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  public ResponseEntity<Order> createOrder(@RequestBody OrderDto orderDto) {
    logger.info("Creating new order");

    try {
      // Create the new order
      Order newOrder = orderService.createOrder(orderDto);

      // Build a created response
      return ResponseEntity.created(new URI("/order/" + newOrder.getId())).body(newOrder);
    } catch (InvalidParameterException e) {
      return new ResponseEntity(
          "A product included in the Order was not found in the inventory or there is not enough stock",
          HttpStatus.BAD_REQUEST);
    } catch (EmptyOrderException e) {
      return new ResponseEntity("The order does not contain any product", HttpStatus.BAD_REQUEST);
    } catch (StockUpdateException e) {
      return new ResponseEntity(
          "Something went wrong when trying to communicate with the inventory service",
          HttpStatus.SERVICE_UNAVAILABLE);
    } catch (URISyntaxException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

package com.ecommerceapp.shipment.controller;

import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.service.ShipmentService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;

@Validated
@RestController
public class ShipmentController {

  @Autowired private ShipmentService shipmentService;

  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "List of Order Shipments",
            response = OrderShipment.class),
        @ApiResponse(code = 500, message = "unexpected server error", response = Error.class)
      })
  @GetMapping("/shipments")
  public List<OrderShipment> getOrderShipments() {
    return shipmentService.findAll();
  }

  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Expected shipment order to a valid request",
            response = OrderShipment.class),
        @ApiResponse(code = 404, message = "Shipment order not found", response = Error.class),
        @ApiResponse(code = 500, message = "unexpected server error", response = Error.class)
      })
  @GetMapping("/shipment/{id}")
  public ResponseEntity<OrderShipment> getShipmentOrder(@PathVariable String id) {

    try {
      OrderShipment shipment = shipmentService.findById(id);

      return ResponseEntity.ok()
          .header("Content-Type", "application/json")
          .location(new URI("/shipment/" + shipment.getId()))
          .body(shipment);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    } catch (URISyntaxException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

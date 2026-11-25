package com.ecommerceapp.shipment.controller;

import com.ecommerceapp.shipment.domain.OrderShipment;
import com.ecommerceapp.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class ShipmentController {

  @Autowired private ShipmentService shipmentService;

  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "List of Order Shipments"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  @GetMapping("/shipments")
  public List<OrderShipment> getOrderShipments() {
    return shipmentService.findAll();
  }

  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expected shipment order to a valid request"),
        @ApiResponse(responseCode = "404", description = "Shipment order not found"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
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

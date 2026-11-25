package com.ecommerceapp.inventory.controller;

import com.ecommerceapp.domain.Product;
import com.ecommerceapp.inventory.dto.request.ChangeStockDto;
import com.ecommerceapp.inventory.dto.request.ProductDto;
import com.ecommerceapp.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityExistsException;
import java.net.URI;
import java.net.URISyntaxException;
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
public class InventoryController {

  private static final Logger logger = LogManager.getLogger(InventoryController.class);

  @Autowired private InventoryService inventoryService;

  /**
   * Returns the product with the specified ID.
   *
   * @param id The ID of the product to retrieve.
   * @return The product with the specified ID.
   */
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Expected product to a valid request"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  @GetMapping("/product/{id}")
  public ResponseEntity<Product> getProduct(@PathVariable String id) {
    try {
      Product product = inventoryService.findById(id);

      return ResponseEntity.ok().location(new URI("/product/" + product.getId())).body(product);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    } catch (URISyntaxException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Returns all products in the database.
   *
   * @return All products in the database.
   */
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expected list of products to a valid request"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  @GetMapping("/products")
  public List<Product> getProducts() {
    return inventoryService.findAll();
  }

  /**
   * Creates a new product.
   *
   * @param productDto The DTO to create a product.
   * @return The created product.
   */
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Product created"),
        @ApiResponse(
            responseCode = "400",
            description = "There is a problem with the request parameters"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  @PostMapping("/product")
  public ResponseEntity<Product> createProduct(@RequestBody @Validated ProductDto productDto) {

    try {
      // Create the new product
      Product newProduct = inventoryService.createProduct(productDto);

      logger.info(
          "Created new product with name: {}, quantity: {}",
          newProduct.getName(),
          newProduct.getQuantity());

      // Build a created response
      return ResponseEntity.created(new URI("/product/" + newProduct.getId())).body(newProduct);
    } catch (EntityExistsException e) {
      return new ResponseEntity(
          "There is a product with the same name already registered", HttpStatus.BAD_REQUEST);
    } catch (URISyntaxException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Product stock updated"),
        @ApiResponse(responseCode = "404", description = "The product referenced was not found"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  @PostMapping("/product/{id}/changeStock")
  public ResponseEntity<?> changeStock(
      @PathVariable String id, @RequestBody ChangeStockDto changeStockDto) {
    try {
      Product product = inventoryService.findById(id);

      inventoryService.updateStock(product, changeStockDto);
      return ResponseEntity.ok().build();
    } catch (NoSuchElementException e) {
      return new ResponseEntity("The product referenced was not found", HttpStatus.NOT_FOUND);
    }
  }

  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Product deleted"),
        @ApiResponse(responseCode = "404", description = "The product referenced was not found"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
      })
  @DeleteMapping("/product/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable String id) {
    logger.info("Deleting product with id: {}", id);
    try {
      inventoryService.deleteProduct(id);
      return ResponseEntity.ok().build();
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    }
  }
}

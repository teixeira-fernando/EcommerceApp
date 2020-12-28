package com.ecommerceapp.inventory.controller;

import com.ecommerceapp.inventory.dto.request.ChangeStockDto;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.service.InventoryService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;

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
        @ApiResponse(
            code = 200,
            message = "Expected product to a valid request",
            response = Product.class),
        @ApiResponse(code = 404, message = "Product not found", response = Error.class),
        @ApiResponse(code = 500, message = "unexpected server error", response = Error.class)
      })
  @GetMapping("/product/{id}")
  public ResponseEntity getProduct(@PathVariable String id) {
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
            code = 200,
            message = "Expected list of products to a valid request",
            response = List.class),
        @ApiResponse(code = 500, message = "unexpected server error", response = Error.class)
      })
  @GetMapping("/products")
  public List<Product> getProducts() {
    return inventoryService.findAll();
  }

  /**
   * Creates a new product.
   *
   * @param product The product to create.
   * @return The created product.
   */
  @ApiResponses(
      value = {
        @ApiResponse(code = 201, message = "Product created", response = Product.class),
        @ApiResponse(
            code = 400,
            message = "There is a problem with the request parameters",
            response = Error.class),
        @ApiResponse(code = 500, message = "unexpected server error", response = Error.class)
      })
  @PostMapping("/product")
  public ResponseEntity createProduct(@RequestBody @Valid Product product) {
    logger.info(
        "Creating new product with name: {}, quantity: {}",
        product.getName(),
        product.getQuantity());

    try {
      // Create the new product
      Product newProduct = inventoryService.createProduct(product);

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
        @ApiResponse(code = 200, message = "Product stock updated", response = Product.class),
        @ApiResponse(
            code = 404,
            message = "The product referenced was not found",
            response = Error.class),
        @ApiResponse(code = 500, message = "unexpected server error", response = Error.class)
      })
  @PostMapping("/product/{id}/changeStock")
  public ResponseEntity changeStock(
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
        @ApiResponse(code = 200, message = "Product deleted", response = Product.class),
        @ApiResponse(
            code = 404,
            message = "The product referenced was not found",
            response = Error.class),
        @ApiResponse(code = 500, message = "unexpected server error", response = Error.class)
      })
  @DeleteMapping("/product/{id}")
  public ResponseEntity deleteProduct(@PathVariable String id) {
    logger.info("Deleting product with id: {}", id);
    try {
      inventoryService.deleteProduct(id);
      return ResponseEntity.ok().build();
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    }
  }
}

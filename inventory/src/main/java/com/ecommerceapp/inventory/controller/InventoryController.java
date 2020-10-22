package com.ecommerceapp.inventory.controller;

import com.ecommerceapp.inventory.dto.request.ChangeStockDto;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.service.InventoryService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  @GetMapping("/product/{id}")
  public ResponseEntity<?> getProduct(@PathVariable String id) {
    try {
      Optional<Product> product = inventoryService.findById(id);

      if (product.isPresent()) {
        return ResponseEntity.ok()
            .location(new URI("/product/" + product.get().getId()))
            .body(product);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (URISyntaxException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Returns all products in the database.
   *
   * @return All products in the database.
   */
  @GetMapping("/products")
  public Iterable<Product> getProducts() {
    return inventoryService.findAll();
  }

  /**
   * Creates a new product.
   *
   * @param product The product to create.
   * @return The created product.
   */
  @PostMapping("/product")
  public ResponseEntity<Product> createProduct(@RequestBody Product product) {
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

  @PostMapping("/product/{id}/changeStock")
  public ResponseEntity<?> changeStock(
      @PathVariable String id, @RequestBody ChangeStockDto changeStockDto) {

    Optional<Product> product = inventoryService.findById(id);
    if (product.isPresent()) {
      inventoryService.updateStock(product.get(), changeStockDto);
    } else {
      return new ResponseEntity("The product referenced was not found", HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/product/{id}")
  public ResponseEntity<Product> deleteProduct(@PathVariable String id) {
    logger.info("Deleting product with id: {}", id);

    // check if product exists
    Optional<Product> product = inventoryService.findById(id);

    if (product.isPresent()) {
      inventoryService.deleteProduct(id);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}

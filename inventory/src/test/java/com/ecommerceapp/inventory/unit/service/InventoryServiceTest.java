package com.ecommerceapp.inventory.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import com.ecommerceapp.inventory.service.InventoryService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {InventoryService.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class InventoryServiceTest {

  /** The service that we want to test. */
  @Autowired private InventoryService service;

  @MockBean private InventoryRepository repository;

  @Test
  @DisplayName("findById - success")
  void testFindByIdSuccess() {
    // Arrange: Setup our mock
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Product product = new Product(productName, quantity, category);
    doReturn(Optional.of(product)).when(repository).findById(product.getId());

    // Act: Call the service method findById
    Optional<Product> returnedProduct = service.findById(product.getId());

    // Assert: verify the returned product
    Assertions.assertTrue(returnedProduct.isPresent(), "Product was not found");
    Assertions.assertEquals(returnedProduct.get(), product, "Product should be the same");
  }

  @Test
  @DisplayName("findById not found")
  void testFindByIdNotFound() {
    // Arrange: Setup our mock
    doReturn(Optional.empty()).when(repository).findById("1");

    // Act: Call the service method findById
    Optional<Product> returnedProduct = service.findById("1");

    // Assert the response
    Assertions.assertFalse(returnedProduct.isPresent(), "Product was found, when it shouldn't be");
  }

  @Test
  @DisplayName("findAll Success")
  void testFindAllSuccess() {
    // Arrange: Setup our mock
    Product product1 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    Product product2 = new Product("Samsung TV Led", 50, Category.ELECTRONICS);
    doReturn(Arrays.asList(product1, product2)).when(repository).findAll();

    // Act: Call the service method findAll
    List<Product> products = service.findAll();

    // Assert the response
    Assertions.assertEquals(2, products.size(), "The product list should return 2 items");
  }

  @Test
  @DisplayName("createProduct success")
  void testCreateProductSuccess() {
    // Arrange: Setup our mock
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Product product = new Product(productName, quantity, category);
    doReturn(product).when(repository).save(any());

    // Act: Call the service method create product
    Product createdProduct = service.createProduct(product);

    // Assert: verify the returned product
    Assertions.assertNotNull(createdProduct, "The saved product should not be null");
    Assertions.assertEquals(createdProduct, product, "Product should be the same");
  }

  @Test
  @DisplayName("createProduct already exists error")
  void testCreateProductAlreadyExists() {
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Product product = new Product(productName, quantity, category);
    doReturn(product).when(repository).save(any());
    doReturn(Optional.of(product)).when(repository).findByName(any());

    Assertions.assertThrows(
        EntityExistsException.class,
        () -> {
          service.createProduct(product);
        });
  }

  @Test
  @DisplayName("updateProduct success")
  void testUpdateProductSuccess() {
    // Arrange: Setup our spy
    String productName = "Samsung TV Led";
    Integer quantity = 50;
    Category category = Category.ELECTRONICS;

    Product product = new Product(productName, quantity, category);
    doReturn(product).when(repository).save(any());

    // Act: Call the services to save a product and then update it
    service.createProduct(product);
    product.setQuantity(60);
    product.setName("Samsung TV Led 42º");
    Product updatedProduct = service.updateProduct(product);

    // Assert: verify the updated product
    Mockito.verify(repository, Mockito.times(2)).save(product);
    Assertions.assertEquals(updatedProduct, product);
  }

  @Test
  @DisplayName("deleteProduct success")
  void testDeleteProductSuccess() {
    // Act
    service.deleteProduct("1");

    // Assert
    Mockito.verify(repository, Mockito.times(1)).deleteById("1");
  }
}

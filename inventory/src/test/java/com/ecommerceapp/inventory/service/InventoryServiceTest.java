package com.ecommerceapp.inventory.service;

import static org.mockito.Mockito.doReturn;

import com.ecommerceapp.inventory.model.Category;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class InventoryServiceTest {

  /** The service that we want to test. */
  @Autowired private InventoryService service;

  /** A mock version of the ReviewRepository for use in our tests. */
  @MockBean private InventoryRepository repository;

  @Test
  @DisplayName("findById sucess")
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
}

package com.ecommerceapp.inventory.service;

import com.ecommerceapp.inventory.dto.request.ChangeStockDto;
import com.ecommerceapp.inventory.dto.request.StockOperation;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

  @Autowired private InventoryRepository repository;

  public Product findById(String id) {
    Optional<Product> product = this.repository.findById(id);
    if (product.isEmpty()) {
      throw new NoSuchElementException();
    }
    return product.get();
  }

  public List<Product> findAll() {
    return this.repository.findAll();
  }

  public Product createProduct(@Valid Product product) {
    Optional<Product> checkIfProductAlreadyExists = repository.findByName(product.getName());

    if (checkIfProductAlreadyExists.isPresent()) {
      throw new EntityExistsException(
          "There is already a product with the same name registered in the inventory");
    }

    return this.repository.save(product);
  }

  public Product updateProduct(@Valid Product product) {
    return this.repository.save(product);
  }

  public void deleteProduct(String id) {
    certifyThatProductExistsInInventory(id);
    this.repository.deleteById(id);
  }

  public int updateStock(@Valid Product product, ChangeStockDto changeStockDto) {
    int newStock = 0;
    certifyThatProductExistsInInventory(product.getId());
    if (changeStockDto.getOperation() == StockOperation.INCREMENT) {
      newStock = product.getQuantity() + changeStockDto.getQuantity();
    } else if (changeStockDto.getOperation() == StockOperation.DECREMENT) {
      newStock = product.getQuantity() - changeStockDto.getQuantity();
    }
    product.setQuantity(newStock);

    this.repository.save(product);
    return newStock;
  }

  public void certifyThatProductExistsInInventory(String id) {
    Optional<Product> productTobeUpdated = repository.findById(id);
    if (productTobeUpdated.isEmpty()) {
      throw new NoSuchElementException(
          "It is not possible to update the stock of a product that doesn't exist in the inventory");
    }
  }
}

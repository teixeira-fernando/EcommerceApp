package com.ecommerceapp.inventory.service;

import com.ecommerceapp.inventory.dto.request.ChangeStockDto;
import com.ecommerceapp.inventory.dto.request.StockOperation;
import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

  @Autowired private InventoryRepository repository;

  public Product findById(String id) {
    Optional<Product> product = this.repository.findById(id);
    if (!product.isPresent()) {
      throw new NoSuchElementException();
    }
    return product.get();
  }

  public List<Product> findAll() {
    return this.repository.findAll();
  }

  public Product createProduct(Product product) {
    Optional<Product> checkIfProductAlreadyExists = repository.findByName(product.getName());

    if (checkIfProductAlreadyExists.isPresent()) {
      throw new EntityExistsException();
    }

    return this.repository.save(product);
  }

  public Product updateProduct(Product product) {
    return this.repository.save(product);
  }

  public void deleteProduct(String id) {
    this.repository.deleteById(id);
  }

  public int updateStock(Product product, ChangeStockDto changeStockDto) {
    int newStock = 0;
    if (changeStockDto.getOperation() == StockOperation.INCREMENT) {
      newStock = product.getQuantity() + changeStockDto.getQuantity();
    } else if (changeStockDto.getOperation() == StockOperation.DECREMENT) {
      newStock = product.getQuantity() - changeStockDto.getQuantity();
    }
    product.setQuantity(newStock);
    this.repository.save(product);
    return newStock;
  }
}

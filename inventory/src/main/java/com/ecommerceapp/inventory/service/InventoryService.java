package com.ecommerceapp.inventory.service;

import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

  private InventoryRepository repository;

  public InventoryService(InventoryRepository repository) {
    this.repository = repository;
  }

  public Optional<Product> findById(String id) {
    return this.repository.findById(id);
  }
}

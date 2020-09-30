package com.ecommerceapp.inventory.repository;

import com.ecommerceapp.inventory.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InventoryRepository extends MongoRepository<Product, String> {
}

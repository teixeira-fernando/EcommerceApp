package com.ecommerceapp.inventory.repository;

import com.ecommerceapp.inventory.model.Product;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface InventoryRepository extends MongoRepository<Product, String> {

  @Query("{'name' : ?0}")
  Optional<Product> findByName(String productName);
}

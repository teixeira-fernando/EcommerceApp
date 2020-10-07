package com.ecommerceapp.inventory.repository;

import com.ecommerceapp.inventory.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends MongoRepository<Product, String> {

    @Query("{'name' : ?0}")
    Optional<Product> findByName(String productName);
}

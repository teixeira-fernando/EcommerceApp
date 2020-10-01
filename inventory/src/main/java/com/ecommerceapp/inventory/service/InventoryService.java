package com.ecommerceapp.inventory.service;

import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public Optional<Product> findById(String id) {
        return this.repository.findById(id);
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
}

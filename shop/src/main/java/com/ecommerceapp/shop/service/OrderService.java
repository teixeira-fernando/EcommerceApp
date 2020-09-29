package com.ecommerceapp.shop.service;

import com.ecommerceapp.inventory.model.Product;
import com.ecommerceapp.inventory.repository.InventoryRepository;
import com.ecommerceapp.shop.model.Order;
import com.ecommerceapp.shop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Optional<Order> findById(String id) {
        return this.repository.findById(id);
    }

    public List<Order> findAll() {
        return this.repository.findAll();
    }

    public Order createOrder(Order order) {
        return this.repository.save(order);
    }

    public Order updateOrder(Order order) {
        return this.repository.save(order);
    }

}

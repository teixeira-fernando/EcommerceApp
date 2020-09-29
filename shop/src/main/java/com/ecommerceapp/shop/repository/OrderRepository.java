package com.ecommerceapp.shop.repository;

import com.ecommerceapp.shop.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

}

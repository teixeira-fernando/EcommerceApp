package com.ecommerceapp.shipment.repository;

import com.ecommerceapp.shipment.domain.OrderShipment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderShipmentRepository extends MongoRepository<OrderShipment, String> {}

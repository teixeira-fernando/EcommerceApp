package com.ecommerceapp.shipment.service;

import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.repository.OrderShipmentRepository;
import com.ecommerceapp.shipment.service.kafka.MessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {

  private static final Logger logger = LogManager.getLogger(ShipmentService.class);

  @Autowired private OrderShipmentRepository repository;

  @Autowired private MessageListener messageListener;

  public OrderShipment createOrderShipment(OrderShipment orderShipment) {
    return repository.save(orderShipment);
  }
}

package com.ecommerceapp.shipment.service;

import com.ecommerceapp.shipment.model.Location;
import com.ecommerceapp.shipment.model.OrderShipment;
import com.ecommerceapp.shipment.repository.OrderShipmentRepository;
import com.ecommerceapp.shipment.service.kafka.MessageListener;
import com.ecommerceapp.shop.model.Order;
import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {

  private static final Logger logger = LogManager.getLogger(ShipmentService.class);

  @Autowired private OrderShipmentRepository repository;

  @Autowired private MessageListener messageListener;

  private Faker faker;

  public ShipmentService() {
    faker = new Faker();
  }

  public OrderShipment findById(String id) {
    Optional<OrderShipment> order = this.repository.findById(id);
    if (order.isEmpty()) {
      throw new NoSuchElementException();
    }

    return order.get();
  }

  public OrderShipment createOrderShipment(Order order) {
    String street = faker.address().streetAddress();
    int number = Integer.parseInt(faker.address().buildingNumber());
    String zipCode = faker.address().zipCode();
    String cityName = faker.address().cityName();
    String state = faker.address().state();
    Location location = new Location(street, number, zipCode, cityName, state);
    OrderShipment orderShipment = new OrderShipment(order, LocalDate.now().plusDays(3), location);
    return repository.save(orderShipment);
  }

  public List<OrderShipment> findAll() {
    return this.repository.findAll();
  }
}

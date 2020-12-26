package com.ecommerceapp.shipment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class ShipmentApplication {

  public static void main(String[] args) {
    SpringApplication.run(ShipmentApplication.class, args);
  }
}

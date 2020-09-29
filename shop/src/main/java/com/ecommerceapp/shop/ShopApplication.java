package com.ecommerceapp.shop;

import com.ecommerceapp.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.ecommerceapp.inventory", "com.ecommerceapp.shop"})
//@EnableMongoRepositories
//@Configuration
public class ShopApplication {

    //@Autowired
    //private InventoryRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}

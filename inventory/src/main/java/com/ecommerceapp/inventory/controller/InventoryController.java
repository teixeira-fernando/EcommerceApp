package com.ecommerceapp.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

  @GetMapping("")
  public String home() {
    return "Hello World Inventory!!";
  }
}

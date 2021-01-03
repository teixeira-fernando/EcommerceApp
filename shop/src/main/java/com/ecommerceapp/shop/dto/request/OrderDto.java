package com.ecommerceapp.shop.dto.request;

import com.ecommerceapp.inventory.model.Product;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class OrderDto {

  @Getter @Setter private List<Product> products;

  public OrderDto(List<Product> products) {
    this.products = products;
  }

  public OrderDto() {
    this.products = new ArrayList<>();
  }
}

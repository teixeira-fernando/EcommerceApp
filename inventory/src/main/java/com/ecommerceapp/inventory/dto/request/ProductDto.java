package com.ecommerceapp.inventory.dto.request;

import com.ecommerceapp.domain.Category;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

public class ProductDto {

  @Getter @Setter @NotNull private String name;

  @Getter @Setter @Positive @NotNull private Integer quantity;

  @Getter @Setter @NotNull private Category category;

  public ProductDto() {}

  public ProductDto(String name, Integer quantity, Category category) {
    this.name = name;
    this.quantity = quantity;
    this.category = category;
  }
}

package com.ecommerceapp.inventory.dto.request;

import com.ecommerceapp.inventory.model.Category;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

public class ProductDto {

  @Id
  @Setter
  @Getter
  private String id;

  @Getter @Setter @NotNull private String name;

  @Getter @Setter @Positive @NotNull private Integer quantity;

  @ApiModelProperty(notes = "Product category according to Enum Category", required = true)
  @Getter
  @Setter
  @NotNull
  private Category category;

  public ProductDto(String name, Integer quantity, Category category) {
    this.name = name;
    this.quantity = quantity;
    this.category = category;
  }

  public ProductDto(String id, String name, Integer quantity, Category category) {
    this.id = id;
    this.name = name;
    this.quantity = quantity;
    this.category = category;
  }
}

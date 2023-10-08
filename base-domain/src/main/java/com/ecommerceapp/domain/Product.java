package com.ecommerceapp.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Document(collection = "Product")
@Entity
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @ApiModelProperty(required = false, notes = "Auto generated id")
  private String id;

  @ApiModelProperty(notes = "Product name", required = true)
  @Getter
  @Setter
  @NotNull
  private String name;

  @ApiModelProperty(notes = "Product quantity", required = true)
  @Getter
  @Positive
  @NotNull
  private Integer quantity;

  @ApiModelProperty(notes = "Product category according to Enum Category", required = true)
  @Getter
  @Setter
  @NotNull
  private Category category;

  public Product() {}

  public Product(String name, Integer quantity, Category category) {
    this.name = name;
    this.quantity = quantity;
    this.category = category;
  }

  public Product(String id, String name, Integer quantity, Category category) {
    this.id = id;
    this.name = name;
    this.quantity = quantity;
    this.category = category;
  }

  @Override
  public String toString() {
    return "Product{"
        + "id='"
        + id
        + '\''
        + ", name="
        + name
        + ",quantity="
        + quantity
        + ", category="
        + category.toString()
        + '}';
  }

  public void setQuantity(@Positive int quantity) {
    if (quantity < 0) {
      throw new ArithmeticException(
          "It is not possible that a product contains a stock lower than 0");
    }
    this.quantity = quantity;
  }
}

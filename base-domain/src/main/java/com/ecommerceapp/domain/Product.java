package com.ecommerceapp.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Document(collection = "Product")
@Entity
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @Schema(description = "Auto generated id")
  private String id;

  @Schema(description = "Product name", requiredMode = Schema.RequiredMode.REQUIRED)
  @Getter
  @Setter
  @NotNull
  private String name;

  @Schema(description = "Product quantity", requiredMode = Schema.RequiredMode.REQUIRED)
  @Getter
  @Positive
  @NotNull
  private Integer quantity;

  @Schema(
      description = "Product category according to Enum Category",
      requiredMode = Schema.RequiredMode.REQUIRED)
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

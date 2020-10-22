package com.ecommerceapp.inventory.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Product")
@Entity
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private String id;

  @Getter @Setter @NotNull private String name;
  @Getter @Setter @Positive @NotNull private Integer quantity;
  @Getter @Setter @NotNull private Category category;

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
}

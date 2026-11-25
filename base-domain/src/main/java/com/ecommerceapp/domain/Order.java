package com.ecommerceapp.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Order")
@Entity
@Repository
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @Setter
  @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Auto generated id")
  private String id;

  @Schema(
      description = "List of products included in this order",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @Getter
  @Setter
  private List<Product> products;

  @Schema(description = "Auto generated status for the order")
  @Getter
  @Setter
  private OrderStatus status;

  @CreatedDate
  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Schema(description = "Auto generated date for the order")
  private Date createDate = new Date();

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Schema(description = "Auto generated date for the order")
  private Date updateDate = new Date();

  public Order(String id, List<Product> products) {
    this.id = id;
    this.products = products;
    this.status = OrderStatus.CREATED;
  }

  public Order(List<Product> products) {
    this.products = products;
    this.status = OrderStatus.CREATED;
  }

  public Order() {
    this.products = new ArrayList<>();
    this.status = OrderStatus.CREATED;
  }
}

package com.ecommerceapp.shop.model;

import com.ecommerceapp.inventory.model.Product;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Order")
@Entity
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @ApiModelProperty(required = false, notes = "Auto generated id")
  private String id;

  @ApiModelProperty(required = true, notes = "List of products included in this order")
  @Getter
  @Setter
  private List<Product> products;

  @ApiModelProperty(required = false, notes = "Auto generated status for the order")
  @Getter
  @Setter
  private OrderStatus status;

  @CreatedDate
  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @ApiModelProperty(required = false, notes = "Auto generated date for the order")
  private Date createDate = new Date(); // initialize created date;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @ApiModelProperty(required = false, notes = "Auto generated date for the order")
  private Date updateDate = new Date(); // initialize updated date;

  public Order(String id, ArrayList<Product> products) {
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

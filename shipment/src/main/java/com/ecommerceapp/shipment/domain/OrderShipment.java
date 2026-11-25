package com.ecommerceapp.shipment.domain;

import com.ecommerceapp.domain.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "OrderShipment")
@Entity
public class OrderShipment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @Schema(description = "Auto generated id")
  private String id;

  @Schema(
      description = "Order related to this shipment",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @Getter
  @Setter
  private Order order;

  @CreatedDate
  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Schema(description = "Auto generated date for the shipment")
  private Date createDate = new Date();

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Schema(description = "Auto generated date for the shipment")
  private Date updateDate = new Date();

  @Getter
  @Schema(description = "Auto generated date for the shipment")
  private LocalDate estimatedDeliveryDate;

  @Getter
  @Setter
  @Schema(description = "Delivery location for this shipment")
  private Location deliveryLocation;

  public OrderShipment(
      String id, Order order, LocalDate estimatedDeliveryDate, Location deliveryLocation) {
    this.id = id;
    this.order = order;
    this.estimatedDeliveryDate = estimatedDeliveryDate;
    this.deliveryLocation = deliveryLocation;
  }

  public OrderShipment(Order order, LocalDate estimatedDeliveryDate, Location deliveryLocation) {
    this.order = order;
    this.estimatedDeliveryDate = estimatedDeliveryDate;
    this.deliveryLocation = deliveryLocation;
  }

  public OrderShipment() {}
}

package com.ecommerceapp.shipment.model;

import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "location")
@Entity
public class Location {

  @Getter @Setter private String street;

  @Getter @Setter private int number;

  @Getter @Setter private String postalCode;

  @Getter @Setter private String city;

  @Getter @Setter private String state;

  public Location(String street, int number, String postalCode, String city, String state) {
    this.street = street;
    this.number = number;
    this.postalCode = postalCode;
    this.city = city;
    this.state = state;
  }

  public Location() {}
}

package com.ecommerceapp.shipment.model;

import lombok.Getter;
import lombok.Setter;

public class Location {

  @Getter @Setter private String street;

  @Getter @Setter private int number;

  @Getter @Setter private int postalCode;

  @Getter @Setter private String city;

  @Getter @Setter private String state;

  public Location(String street, int number, int postalCode, String city, String state) {
    this.street = street;
    this.number = number;
    this.postalCode = postalCode;
    this.city = city;
    this.state = state;
  }
}

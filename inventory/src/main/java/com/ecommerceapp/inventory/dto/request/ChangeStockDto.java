package com.ecommerceapp.inventory.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class ChangeStockDto {

  @NotNull @Positive @Getter int quantity;
  @NotNull @Getter StockOperation operation;

  public ChangeStockDto(@Positive int quantity, StockOperation operation) {
    this.quantity = quantity;
    this.operation = operation;
  }
}

package com.ecommerceapp.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

public class ChangeStockDto {

  @NotNull @Positive @Getter int quantity;
  @NotNull @Getter StockOperation operation;

  public ChangeStockDto(@Positive int quantity, StockOperation operation) {
    this.quantity = quantity;
    this.operation = operation;
  }
}

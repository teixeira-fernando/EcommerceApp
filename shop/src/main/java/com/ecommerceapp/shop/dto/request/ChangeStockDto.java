package com.ecommerceapp.shop.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;

public class ChangeStockDto {

  @NotNull @Positive @Getter int quantity;
  @NotNull @Getter StockOperation operation;

  public ChangeStockDto(@Positive int quantity, StockOperation operation) {
    this.quantity = quantity;
    this.operation = operation;
  }
}

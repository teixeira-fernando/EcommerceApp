package com.ecommerceapp.shipment;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI api() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Order Shipment Backend APIs")
                .description("Module used to manage the shipment of all the orders")
                .version("1.0"));
  }
}

package com.ecommerceapp.inventory;

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
                .title("Inventory/Product Backend APIs")
                .description("Module used to manage the products in stock")
                .version("1.0"));
  }
}

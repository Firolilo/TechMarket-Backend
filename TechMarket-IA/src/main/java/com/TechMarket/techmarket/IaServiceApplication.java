package com.techmarket.techmarket;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API TechMarket-AI", version = "v1"))
public class IaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IaServiceApplication.class, args);
    }
}

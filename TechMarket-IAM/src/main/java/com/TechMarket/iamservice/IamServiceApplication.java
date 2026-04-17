package com.techmarket.iamservice;

import com.techmarket.iamservice.config.security.JwtProperties;
import com.techmarket.iamservice.config.security.TokenProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(
        basePackages = {
            "com.techmarket.core.iam.infrastructure.persistence.entity",
            "com.techmarket.iamservice.infrastructure.persistence.entity"
        })
@EnableJpaRepositories(
        basePackages = {
            "com.techmarket.core.iam.infrastructure.persistence.repository",
            "com.techmarket.iamservice.infrastructure.persistence.repository"
        })
@EnableConfigurationProperties({JwtProperties.class, TokenProperties.class})
public class IamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }
}

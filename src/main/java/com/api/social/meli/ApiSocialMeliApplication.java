package com.api.social.meli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.api.social.meli.repository.mysql")
public class ApiSocialMeliApplication {


    public static void main(String[] args) {
        SpringApplication.run(ApiSocialMeliApplication.class, args);
    }

}

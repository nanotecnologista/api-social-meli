package com.api.social.meli.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Social Meli")
                        .version("1.0.0")
                        .description("API REST para rede social de vendedores e compradores\n\n" +
                                "**Autenticação:**\n" +
                                "A maioria dos endpoints requer autenticação via header `X-user-id`. " +
                                "Este header deve conter o ID do usuário autenticado.\n\n" +
                                "**Endpoints públicos (não requerem X-user-id):**\n" +
                                "- POST /auth/register\n" +
                                "- POST /auth/login\n" +
                                "- GET /categories\n" +
                                "- GET /categories/{categoryId}\n" +
                                "- GET /products/{productId}\n" +
                                "- GET /products/promo-pub/count\n" +
                                "- GET /products/promo-pub/list\n" +
                                "- GET /products/followed/{userId}/list\n" +
                                "- GET /users/{userId}/followers/count\n" +
                                "- GET /users/{userId}/followers/list\n" +
                                "- GET /users/{userId}/followed/list")
                        .contact(new Contact()
                                .name("API Social Meli Team")
                                .email("api@socialmeli.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento")
                ))
                .components(new Components()
                        .addParameters("X-user-id", new Parameter()
                                .in("header")
                                .name("X-user-id")
                                .description("ID do usuário autenticado")
                                .required(true)
                                .schema(new StringSchema())
                                .example("1")));
    }
}

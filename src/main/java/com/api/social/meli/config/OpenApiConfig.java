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
                        .description(buildApiDescription())
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
                                .description("ID do usuário autenticado. Obrigatório para endpoints protegidos.")
                                .required(true)
                                .schema(new StringSchema())
                                .example("1")));
    }

    private String buildApiDescription() {
        return """
                # API Social Meli - Documentação Completa para Frontend
                
                API REST para rede social de vendedores e compradores do Mercado Livre.
                
                ## 🔐 Autenticação
                
                A maioria dos endpoints requer autenticação via header HTTP customizado:
                
                ```
                X-user-id: <ID_DO_USUARIO>
                ```
                
                **Exemplo:**
                ```
                X-user-id: 1
                ```
                
                ### Endpoints Públicos (não requerem autenticação):
                
                **Autenticação:**
                - `POST /auth/register` - Registrar novo usuário
                - `POST /auth/login` - Fazer login
                
                **Categorias:**
                - `GET /categories` - Listar todas as categorias
                - `GET /categories/{categoryId}` - Buscar categoria por ID
                
                **Produtos:**
                - `GET /products/{productId}` - Buscar produto por ID
                
                **Promoções:**
                - `GET /products/promo-pub/count?user_id={userId}` - Contar promoções de um vendedor
                - `GET /products/promo-pub/list?user_id={userId}` - Listar promoções de um vendedor
                
                **Timeline:**
                - `GET /products/followed/{userId}/list` - Timeline de produtos dos vendedores seguidos
                
                **Relacionamentos:**
                - `GET /users/{userId}/followers/count` - Contar seguidores de um vendedor
                - `GET /users/{userId}/followers/list` - Listar seguidores de um vendedor
                - `GET /users/{userId}/followed/list` - Listar vendedores seguidos por um usuário
                
                ### Endpoints Protegidos (requerem X-user-id):
                
                Todos os outros endpoints requerem o header `X-user-id` com o ID do usuário autenticado.
                
                ## 📊 Paginação
                
                Endpoints que suportam paginação aceitam os seguintes parâmetros:
                
                - `page` (int, default: 0) - Número da página (começa em 0)
                - `size` (int, default: 10) - Quantidade de itens por página
                - `sort` (string) - Campo e direção de ordenação (ex: "name,asc" ou "date,desc")
                
                **Exemplo:**
                ```
                GET /users/1/followers/list?page=0&size=20&sort=name,asc
                ```
                
                ## 🔄 Ordenação
                
                Alguns endpoints suportam ordenação customizada via parâmetro `order`:
                
                **Para listas de usuários (seguidores/seguidos):**
                - `name_asc` - Alfabética crescente (A-Z)
                - `name_desc` - Alfabética decrescente (Z-A)
                
                **Para timeline de posts:**
                - `date_asc` - Data crescente (mais antigos primeiro)
                - `date_desc` - Data decrescente (mais recentes primeiro) - **padrão**
                
                **Exemplo:**
                ```
                GET /products/followed/1/list?order=date_desc
                ```
                
                ## 📝 Formato de Datas
                
                Todas as datas devem ser enviadas no formato: `dd-MM-yyyy`
                
                **Exemplo:** `29-04-2021`
                
                ## ⚠️ Códigos de Status HTTP
                
                - `200 OK` - Requisição bem-sucedida
                - `201 Created` - Recurso criado com sucesso
                - `204 No Content` - Requisição bem-sucedida sem conteúdo de retorno
                - `400 Bad Request` - Dados inválidos ou erro de validação
                - `401 Unauthorized` - Usuário não autenticado
                - `403 Forbidden` - Usuário não tem permissão para a ação
                - `404 Not Found` - Recurso não encontrado
                - `500 Internal Server Error` - Erro interno do servidor
                
                ## 🎭 Roles (Papéis de Usuário)
                
                - `CUSTOMER` - Cliente (pode seguir vendedores, ver produtos)
                - `SELLER` - Vendedor (pode publicar produtos e promoções)
                
                ## 💡 Dicas para Desenvolvimento Frontend
                
                1. **Armazene o X-user-id** após login/registro para usar em requisições subsequentes
                2. **Implemente tratamento de erros** para todos os códigos de status
                3. **Use paginação** para listas grandes de dados
                4. **Valide dados no frontend** antes de enviar para a API
                5. **Implemente loading states** durante chamadas à API
                6. **Cache dados** quando apropriado (ex: lista de categorias)
                
                ---
                
                Para mais detalhes sobre cada endpoint, consulte a documentação específica abaixo.
                """;
    }
}

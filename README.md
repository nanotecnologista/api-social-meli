# 🛍️ API Social Meli

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

API REST para rede social de vendedores e compradores do Mercado Livre, permitindo que compradores sigam seus vendedores favoritos e acompanhem suas publicações de produtos, incluindo promoções exclusivas.

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura](#-arquitetura)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação e Configuração](#-instalação-e-configuração)
- [Executando o Projeto](#-executando-o-projeto)
- [Documentação da API](#-documentação-da-api)
- [Endpoints Principais](#-endpoints-principais)
- [Autenticação](#-autenticação)
- [Testes](#-testes)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Banco de Dados](#-banco-de-dados)
- [Contribuindo](#-contribuindo)
- [Licença](#-licença)

## 🎯 Sobre o Projeto

O **SocialMeli** é uma plataforma inovadora que aproxima compradores e vendedores, permitindo que os compradores sigam seus vendedores favoritos e recebam atualizações sobre novos produtos e promoções exclusivas.

### Objetivos

- Permitir que compradores sigam vendedores específicos
- Exibir publicações de produtos dos vendedores seguidos
- Gerenciar produtos com e sem promoções
- Fornecer estatísticas de seguidores
- Ordenar e filtrar resultados de forma eficiente

## ✨ Funcionalidades

### Gestão de Usuários e Relacionamentos

- ✅ **Seguir vendedores**: Usuários podem seguir seus vendedores favoritos
- ✅ **Deixar de seguir**: Opção para parar de seguir um vendedor
- ✅ **Listar seguidores**: Visualizar quem segue um vendedor
- ✅ **Listar seguidos**: Ver todos os vendedores que um usuário segue
- ✅ **Contagem de seguidores**: Estatísticas de quantos seguidores um vendedor possui
- ✅ **Ativar vendedor**: Transformar usuário comum em vendedor

### Gestão de Produtos

- ✅ **CRUD completo de produtos**: Criar, ler, atualizar e deletar produtos
- ✅ **Produtos do usuário**: Listar produtos próprios com paginação
- ✅ **Categorização**: Organização por categorias

### Publicações (Posts)

- ✅ **Publicar produtos**: Criar publicações de produtos normais
- ✅ **Publicações promocionais**: Criar posts com descontos especiais
- ✅ **Timeline**: Ver publicações dos vendedores seguidos (últimas 2 semanas)
- ✅ **Listar promoções**: Ver todas as promoções de um vendedor
- ✅ **Contar promoções**: Quantidade de produtos em promoção

### Ordenação e Filtros

- ✅ **Ordenação alfabética**: Ascendente e descendente (seguidores/seguidos)
- ✅ **Ordenação por data**: Crescente e decrescente (timeline)
- ✅ **Paginação**: Suporte a paginação em listas extensas

### Autenticação e Autorização

- ✅ **Sistema de autenticação**: Via header `X-user-id`
- ✅ **Controle de permissões**: Baseado em roles (BUYER, SELLER, ADMIN)
- ✅ **Registro e login**: Endpoints públicos para autenticação

## 🚀 Tecnologias Utilizadas

### Backend

- **Java 21** - Linguagem de programação
- **Spring Boot 3.4.2** - Framework principal
- **Spring Data JPA** - Persistência de dados relacionais
- **Spring Data MongoDB** - Persistência de dados NoSQL
- **Hibernate** - ORM para MySQL
- **Flyway** - Versionamento de banco de dados

### Bancos de Dados

- **MySQL 8.0** - Banco relacional (usuários, produtos, categorias)
- **MongoDB 8.2** - Banco NoSQL (posts, relacionamentos de seguir)

### Documentação

- **SpringDoc OpenAPI 2.7.0** - Documentação automática da API
- **Swagger UI** - Interface interativa para testar endpoints

### Segurança e Validação

- **Spring Security Crypto** - Criptografia de senhas
- **Jakarta Validation** - Validação de dados de entrada
- **Hibernate Validator** - Validações customizadas

### Ferramentas de Desenvolvimento

- **Lombok** - Redução de código boilerplate
- **Docker Compose** - Orquestração de containers
- **Maven** - Gerenciamento de dependências
- **Spring DevTools** - Hot reload durante desenvolvimento

### Testes

- **JUnit 5** - Framework de testes
- **Spring Boot Test** - Testes de integração
- **Testcontainers** - Testes com containers Docker
- **HSQLDB** - Banco em memória para testes

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas (Layered Architecture):

```
┌─────────────────────────────────────┐
│         Controllers                 │  ← Camada de Apresentação
├─────────────────────────────────────┤
│         Services                    │  ← Camada de Negócio
├─────────────────────────────────────┤
│         Repositories                │  ← Camada de Dados
├─────────────────────────────────────┤
│    MySQL          MongoDB           │  ← Persistência
└─────────────────────────────────────┘
```

### Padrões Utilizados

- **DTO (Data Transfer Object)**: Separação entre entidades e objetos de transferência
- **Repository Pattern**: Abstração da camada de dados
- **Service Layer**: Lógica de negócio centralizada
- **Dependency Injection**: Inversão de controle via Spring
- **Builder Pattern**: Construção de objetos complexos (via Lombok)

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 21** ou superior ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **Docker** e **Docker Compose** ([Download](https://www.docker.com/get-started))
- **Git** ([Download](https://git-scm.com/downloads))

### Verificar Instalações

```bash
java -version    # Deve mostrar Java 21
mvn -version     # Deve mostrar Maven 3.8+
docker --version # Deve mostrar Docker
```

## 🔧 Instalação e Configuração

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/api-social-meli.git
cd api-social-meli
```

### 2. Iniciar os Bancos de Dados

O projeto usa Docker Compose para gerenciar MySQL e MongoDB:

```bash
docker-compose up -d
```

Isso iniciará:
- **MySQL** na porta `3306`
  - Database: `social_meli`
  - User: `user` / Password: `user123`
  - Root Password: `root123`

- **MongoDB** na porta `27017`
  - Database: `socialmeli`
  - User: `root` / Password: `root123`

### 3. Verificar Containers

```bash
docker-compose ps
```

Ambos os containers devem estar com status "Up".

### 4. Compilar o Projeto

```bash
mvn clean install
```

Ou para pular os testes:

```bash
mvn clean install -DskipTests
```

## ▶️ Executando o Projeto

### Modo Desenvolvimento

```bash
mvn spring-boot:run
```

Ou via IDE (IntelliJ IDEA, Eclipse, VS Code):
- Execute a classe `ApiSocialMeliApplication.java`

### Modo Produção

```bash
# Compilar
mvn clean package -DskipTests

# Executar JAR
java -jar target/api_social_meli-0.0.1-SNAPSHOT.jar
```

### Verificar Aplicação

A aplicação estará disponível em: `http://localhost:8080`

Logs de inicialização devem mostrar:
```
Started ApiSocialMeliApplication in X.XXX seconds
```

## 📚 Documentação da API

### Swagger UI (Recomendado)

Acesse a documentação interativa em:

**🔗 http://localhost:8080/swagger-ui.html**

A interface Swagger permite:
- Visualizar todos os endpoints
- Testar requisições diretamente no navegador
- Ver schemas de request/response
- Entender parâmetros e validações

### OpenAPI JSON

Especificação OpenAPI 3.0 disponível em:

**🔗 http://localhost:8080/v3/api-docs**

## 🔌 Endpoints Principais

### Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/auth/register` | Registrar novo usuário | ❌ |
| POST | `/auth/login` | Fazer login | ❌ |

### Usuários e Relacionamentos

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/users/{id}/activate-seller` | Ativar usuário como vendedor | ✅ |
| POST | `/users/{userId}/follow/{userIdToFollow}` | Seguir vendedor | ✅ |
| POST | `/users/{userId}/unfollow/{userIdToUnfollow}` | Deixar de seguir | ✅ |
| GET | `/users/{userId}/followers/count` | Contar seguidores | ❌ |
| GET | `/users/{userId}/followers/list` | Listar seguidores | ❌ |
| GET | `/users/{userId}/followed/list` | Listar seguidos | ❌ |

### Categorias

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/v1/categories` | Criar categoria | ❌ |
| GET | `/api/v1/categories` | Listar categorias | ❌ |
| GET | `/api/v1/categories/{id}` | Buscar categoria | ❌ |

### Produtos

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/products` | Criar produto | ✅ |
| GET | `/products/{id}` | Buscar produto | ❌ |
| PUT | `/products/{id}` | Atualizar produto | ✅ |
| DELETE | `/products/{id}` | Deletar produto | ✅ |
| GET | `/products/me` | Listar meus produtos | ✅ |
| GET | `/products/me/list` | Listar meus produtos (paginado) | ✅ |

### Posts/Publicações

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/products/publish` | Publicar produto | ✅ |
| POST | `/products/promo-pub` | Publicar promoção | ✅ |
| GET | `/products/promo-pub/count?user_id={id}` | Contar promoções | ❌ |
| GET | `/products/promo-pub/list?user_id={id}` | Listar promoções | ❌ |
| GET | `/products/followed/{userId}/list` | Timeline de seguidos | ❌ |

### Parâmetros de Ordenação

**Seguidores/Seguidos:**
- `?order=name_asc` - Alfabética crescente
- `?order=name_desc` - Alfabética decrescente

**Timeline:**
- `?order=date_asc` - Data crescente (antiga → recente)
- `?order=date_desc` - Data decrescente (recente → antiga)

**Paginação:**
- `?page=0&size=10&sort=name,asc`

## 🔐 Autenticação

A API usa autenticação via **header HTTP customizado**.

### Header Obrigatório

Para endpoints protegidos, inclua:

```http
X-user-id: 1
```

### Exemplo com cURL

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "X-user-id: 1" \
  -d '{
    "name": "Notebook Gamer",
    "description": "RTX 4090",
    "price": 15000.00,
    "categoryId": 1
  }'
```

### Endpoints Públicos (sem autenticação)

- `POST /auth/register`
- `POST /auth/login`
- `GET /categories/**`
- `GET /products/{id}`
- `GET /products/promo-pub/**`
- `GET /products/followed/**`
- `GET /users/{userId}/followers/**`
- `GET /users/{userId}/followed/**`

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Testes Específicos

```bash
# Testes unitários
mvn test -Dtest=*Test

# Testes de integração
mvn test -Dtest=*IT
```

### Cobertura de Testes

O projeto inclui:
- ✅ Testes unitários para services
- ✅ Testes de integração para controllers
- ✅ Testes com Testcontainers (MongoDB)
- ✅ Validações de dados de entrada

### Exemplo de Teste

```java
@Test
void shouldFollowUserSuccessfully() {
    // Given
    Long userId = 1L;
    Long userToFollowId = 2L;
    
    // When & Then
    assertDoesNotThrow(() -> 
        followService.followUser(userId, userToFollowId)
    );
}
```

## 📁 Estrutura do Projeto

```
api-social-meli/
├── src/
│   ├── main/
│   │   ├── java/com/api/social/meli/
│   │   │   ├── config/              # Configurações (OpenAPI, Web, Interceptors)
│   │   │   ├── controller/          # Controllers REST
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── auth/           # DTOs de autenticação
│   │   │   │   ├── category/       # DTOs de categorias
│   │   │   │   ├── common/         # DTOs comuns (paginação)
│   │   │   │   ├── post/           # DTOs de posts
│   │   │   │   ├── product/        # DTOs de produtos
│   │   │   │   └── user/           # DTOs de usuários
│   │   │   ├── exception/           # Exceções customizadas
│   │   │   ├── model/               # Entidades de domínio
│   │   │   │   ├── mongo/          # Documentos MongoDB
│   │   │   │   └── mysql/          # Entidades JPA
│   │   │   ├── repository/          # Repositórios
│   │   │   │   ├── mongo/          # Repositórios MongoDB
│   │   │   │   └── mysql/          # Repositórios JPA
│   │   │   ├── service/             # Lógica de negócio
│   │   │   └── ApiSocialMeliApplication.java
│   │   └── resources/
│   │       ├── db/migration/        # Scripts Flyway
│   │       └── application.properties
│   └── test/
│       └── java/com/api/social/meli/
│           ├── controller/          # Testes de integração
│           └── service/             # Testes unitários
├── desafio/                         # Documentação do desafio
├── docker-compose.yaml              # Configuração Docker
├── pom.xml                          # Dependências Maven
└── README.md
```

## 🗄️ Banco de Dados

### MySQL - Dados Relacionais

**Tabelas principais:**

- `users` - Usuários do sistema
- `roles` - Papéis (BUYER, SELLER, ADMIN)
- `user_roles` - Relacionamento usuário-papel
- `categories` - Categorias de produtos
- `products` - Produtos cadastrados

**Migrations:** Gerenciadas pelo Flyway em `src/main/resources/db/migration/`

### MongoDB - Dados NoSQL

**Collections:**

- `posts` - Publicações de produtos
- `follows` - Relacionamentos de seguir

**Vantagens:**
- Flexibilidade para dados de posts
- Performance em consultas de timeline
- Escalabilidade horizontal

### Diagrama Simplificado

```
MySQL:
┌─────────┐     ┌──────────┐     ┌────────────┐
│  users  │────<│user_roles│>────│   roles    │
└─────────┘     └──────────┘     └────────────┘
     │
     │
     ▼
┌──────────┐     ┌────────────┐
│ products │────>│ categories │
└──────────┘     └────────────┘

MongoDB:
┌─────────┐     ┌─────────┐
│  posts  │     │ follows │
└─────────┘     └─────────┘
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código

- Siga as convenções Java
- Use Lombok para reduzir boilerplate
- Documente métodos públicos
- Escreva testes para novas funcionalidades
- Mantenha cobertura de testes acima de 70%

## 📄 Licença

Este projeto está sob a licença Apache 2.0. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 📞 Suporte

Para questões e suporte:

- 📧 Email: api@socialmeli.com
- 📖 Documentação: http://localhost:8080/swagger-ui.html
- 🐛 Issues: [GitHub Issues](https://github.com/seu-usuario/api-social-meli/issues)

---

**Desenvolvido com ❤️ para o Bootcamp Mercado Livre**

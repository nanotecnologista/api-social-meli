package com.api.social.meli.controller;

import com.api.social.meli.dto.auth.LoginRequest;
import com.api.social.meli.dto.auth.LoginResponse;
import com.api.social.meli.dto.auth.RegisterRequest;
import com.api.social.meli.dto.auth.RegisterResponse;
import com.api.social.meli.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para registro e autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar novo usuário",
            description = """  
                    Cria uma nova conta de usuário no sistema.
                    
                    **Validações:**
                    - Nome: obrigatório, máximo 100 caracteres
                    - Email: obrigatório, formato válido, único no sistema
                    - Senha: obrigatório, mínimo 6 caracteres
                    
                    **Exemplo de Request:**
                    ```json
                    {
                      "name": "João Silva",
                      "email": "joao.silva@email.com",
                      "password": "senha123"
                    }
                    ```
                    
                    **Exemplo de Response (201 Created):**
                    ```json
                    {
                      "id": 1,
                      "name": "João Silva",
                      "email": "joao.silva@email.com",
                      "roles": ["CUSTOMER"]
                    }
                    ```
                    
                    **Observações:**
                    - Por padrão, novos usuários recebem a role CUSTOMER
                    - O ID retornado deve ser usado como X-user-id em requisições futuras
                    - A senha é criptografada antes de ser armazenada
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário registrado com sucesso",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou usuário já existe. Possíveis erros: email já cadastrado, formato de email inválido, senha muito curta"
            )
    })
    public RegisterResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Fazer login",
            description = """  
                    Autentica um usuário e retorna suas informações completas.
                    
                    **Exemplo de Request:**
                    ```json
                    {
                      "email": "joao.silva@email.com",
                      "password": "senha123"
                    }
                    ```
                    
                    **Exemplo de Response (200 OK):**
                    ```json
                    {
                      "id": 1,
                      "name": "João Silva",
                      "email": "joao.silva@email.com",
                      "roles": ["CUSTOMER", "SELLER"]
                    }
                    ```
                    
                    **Fluxo de Autenticação:**
                    1. Envie email e senha
                    2. Receba o ID do usuário e suas roles
                    3. Armazene o ID para usar como X-user-id em requisições futuras
                    4. Use as roles para controlar permissões no frontend
                    
                    **Observações:**
                    - O ID retornado deve ser armazenado (localStorage/sessionStorage)
                    - Use o ID como valor do header X-user-id em requisições protegidas
                    - Verifique as roles para habilitar/desabilitar funcionalidades
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas. Email não encontrado ou senha incorreta"
            )
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
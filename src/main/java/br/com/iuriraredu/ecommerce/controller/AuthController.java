package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.dto.AuthenticationDTO;
import br.com.iuriraredu.ecommerce.dto.LoginResponseDTO;
import br.com.iuriraredu.ecommerce.dto.RegisterDTO;
import br.com.iuriraredu.ecommerce.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Gerenciamento de usuários (Realiza acesso ao sistema e Cadastra novo usuário).")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Realizar autenticação (Login)",
            description = "Autentica um usuário no sistema utilizando credenciais válidas (e-mail e senha) e retorna um token JWT para acesso aos endpoints protegidos."
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso, retornando o token de acesso")
    @ApiResponse(responseCode = "400", description = "Dados de autenticação inválidos ou faltando preenchimento")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas (usuário ou senha incorretos)")
    @ApiResponse(responseCode = "406", description = "'Accept' incorreto")
    @ApiResponse(responseCode = "415", description = "'Content-Type' incorreto")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        LoginResponseDTO token = authService.login(data);
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Registrar novo usuário",
            description = "Cadastra um novo usuário no sistema com base nos dados informados no corpo da requisição."
    )
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou e-mail já cadastrado")
    @ApiResponse(responseCode = "406", description = "'Accept' incorreto")
    @ApiResponse(responseCode = "415", description = "'Content-Type' incorreto")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        authService.register(data);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping(value = "/register/admin", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Registrar novo usuário com role customizada (restrito a administradores)",
            description = "Cadastra um novo usuário respeitando a role informada no corpo da requisição. Diferente de /auth/register, este endpoint exige autenticação com ROLE_ADMIN."
    )
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou e-mail já cadastrado")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Autenticado, mas sem permissão de administrador")
    public ResponseEntity<Void> registerWithRole(@RequestBody @Valid RegisterDTO data) {
        authService.registerWithRole(data);
        return ResponseEntity.status(CREATED).build();
    }
}
package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.dto.AuthenticationDTO;
import br.com.iuriraredu.ecommerce.dto.LoginResponseDTO;
import br.com.iuriraredu.ecommerce.dto.RegisterDTO;
import br.com.iuriraredu.ecommerce.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/login")
    @Operation(
            summary = "Realizar autenticação (Login)",
            description = "Autentica um usuário no sistema utilizando credenciais válidas (e-mail e senha) e retorna um token JWT para acesso aos endpoints protegidos."
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso, retornando o token de acesso")
    @ApiResponse(responseCode = "400", description = "Dados de autenticação inválidos ou faltando preenchimento")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas (usuário ou senha incorretos)")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        LoginResponseDTO token = authService.login(data);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Cadastra um novo usuário no sistema com base nos dados informados no corpo da requisição."
    )
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou e-mail já cadastrado")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        authService.register(data);
        return ResponseEntity.status(CREATED).build();
    }
}
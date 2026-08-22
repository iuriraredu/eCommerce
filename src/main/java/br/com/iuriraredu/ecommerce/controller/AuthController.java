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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User management (Log in to the system and register new users).")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate (Login)",
            description = "Authenticates a user in the system using valid credentials (email and password) and returns a JWT token to access protected endpoints."
    )
    @ApiResponse(responseCode = "200", description = "Login successful, returning the access token")
    @ApiResponse(responseCode = "400", description = "Invalid or missing authentication data")
    @ApiResponse(responseCode = "401", description = "Invalid credentials (incorrect login or password)")
    @ApiResponse(responseCode = "406", description = "Incorrect 'Accept' header")
    @ApiResponse(responseCode = "415", description = "Incorrect 'Content-Type' header")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid final AuthenticationDTO data) {
        LoginResponseDTO token = authService.login(data);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user in the system using the data provided in the request body."
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data provided or email already registered")
    @ApiResponse(responseCode = "406", description = "Incorrect 'Accept' header")
    @ApiResponse(responseCode = "415", description = "Incorrect 'Content-Type' header")
    public ResponseEntity<Void> register(@RequestBody @Valid final RegisterDTO data) {
        authService.register(data);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/register/admin")
    @Operation(
            summary = "Register a new user with a custom role (admin only)",
            description = "Registers a new user honoring the role provided in the request body. Unlike /auth/register, this endpoint requires authentication with ROLE_ADMIN."
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data provided or email already registered")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "403", description = "Authenticated, but without admin permission")
    public ResponseEntity<Void> registerWithRole(@RequestBody @Valid final RegisterDTO data) {
        authService.registerWithRole(data);
        return ResponseEntity.status(CREATED).build();
    }
}
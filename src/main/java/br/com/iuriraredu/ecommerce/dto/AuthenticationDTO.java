package br.com.iuriraredu.ecommerce.dto;

// Esse DTO tem o @Valid na AuthController mas não possui validação.
public record AuthenticationDTO(String login, String password) {
}

package br.com.iuriraredu.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(

        @NotBlank(message = "Login is required")
        String login,

        @NotBlank(message = "Password is required")
        String password
) {
}

package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(name = "RegisterDTO", description = "Data transfer object for registering new users")
public record RegisterDTO(

        @NotBlank(message = "Login is required")
        @Size(max = 150, message = "Login must be at most 150 characters")
        @Schema(description = "Email or login used for authentication in the system", example = "test@email.com", requiredMode = REQUIRED)
        String login,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Schema(description = "User password (will be encrypted with Argon2id)", example = "a-strong-password-123", requiredMode = REQUIRED)
        String password,

        @NotNull(message = "Role is required")
        @Schema(description = "User access role in the system", example = "ADMIN", requiredMode = REQUIRED)
        UserRole role
) {
}
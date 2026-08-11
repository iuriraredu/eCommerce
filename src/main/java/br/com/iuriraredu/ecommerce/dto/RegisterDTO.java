package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(name = "RegisterDTO", description = "Data transfer object for registering new users")
public record RegisterDTO(

        @Schema(description = "Email or login used for authentication in the system", example = "test@email.com", requiredMode = REQUIRED)
        String login,

        @Schema(description = "User password (will be encrypted with Argon2id)", example = "123456", requiredMode = REQUIRED)
        String password,

        @Schema(description = "User access role in the system", example = "ADMIN", requiredMode = REQUIRED)
        UserRole role
) {
}
package br.com.iuriraredu.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

public record ClientRequestDTO(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        @Size(max = 150, message = "Email must be at most 150 characters")
        String email,

        @NotBlank(message = "CPF is required")
        @CPF(message = "Invalid CPF")
        String cpf,

        @Valid
        List<AddressRequestDTO> addresses,

        @Valid
        List<PhoneRequestDTO> phones
) {
}

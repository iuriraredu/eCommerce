package br.com.iuriraredu.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequestDTO(

        @NotBlank(message = "Street is required")
        @Size(max = 150, message = "Street must be at most 150 characters")
        String street,

        @NotBlank(message = "Number is required")
        @Size(max = 20, message = "Number must be at most 20 characters")
        String number,

        @Size(max = 100, message = "Complement must be at most 100 characters")
        String complement,

        @NotBlank(message = "Neighborhood is required")
        @Size(max = 100, message = "Neighborhood must be at most 100 characters")
        String neighborhood,

        @NotBlank(message = "Zip code is required")
        @Size(min = 8, max = 9, message = "Zip code must be 8 digits (with or without a hyphen)")
        String cep
) {
}

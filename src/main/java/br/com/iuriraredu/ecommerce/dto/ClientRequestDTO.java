package br.com.iuriraredu.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ClientRequestDTO(

        @NotBlank(message = "O nome é obrigatório") // Não valida o limite de caracteres mínimo e máximo.
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "O CPF é obrigatório")
        String cpf, // Não está validando se realmente é um CPF ou qualquer coisa que o cara digitar

        @Valid
        List<AddressDTO> addresses,

        @Valid
        List<PhoneDTO> phones
) {
}
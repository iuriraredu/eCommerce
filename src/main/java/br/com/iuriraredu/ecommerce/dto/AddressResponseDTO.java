package br.com.iuriraredu.ecommerce.dto;

public record AddressResponseDTO(
        Long id,
        String street,
        String number,
        String complement,
        String neighborhood,
        String cep
) {
}

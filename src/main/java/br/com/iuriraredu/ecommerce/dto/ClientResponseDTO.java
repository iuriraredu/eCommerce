package br.com.iuriraredu.ecommerce.dto;

import java.util.List;

public record ClientResponseDTO(
        Long id,
        String name,
        String email,
        String cpf,
        List<AddressResponseDTO> addresses,
        List<PhoneResponseDTO> phones
) {
}

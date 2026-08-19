package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.Client;

import java.util.List;

public record ClientResponseDTO(
        Long id,
        String name,
        String email,
        String cpf,
        List<AddressDTO> addresses,
        List<PhoneDTO> phones
) {
    public static ClientResponseDTO fromEntity(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getCpf(),
                client.getAddresses() == null ? List.of() : client.getAddresses().stream().map(AddressDTO::fromEntity).toList(),
                client.getPhones() == null ? List.of() : client.getPhones().stream().map(PhoneDTO::fromEntity).toList()
        );
    }
}
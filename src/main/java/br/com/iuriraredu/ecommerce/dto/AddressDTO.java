package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.Address;
import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
        Long id,

        @NotBlank(message = "A rua é obrigatória")
        String street,

        @NotBlank(message = "O número é obrigatório")
        String number,

        String complement,

        @NotBlank(message = "O bairro é obrigatório")
        String neighborhood,

        @NotBlank(message = "O CEP é obrigatório")
        String cep
) {
    public static AddressDTO fromEntity(Address address) {
        return new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCep()
        );
    }

    // id é sempre ignorado aqui de propósito: endereço novo nunca deve nascer com id vindo do cliente da API.
    public Address toEntity() {
        Address address = new Address();
        address.setStreet(street());
        address.setNumber(number());
        address.setComplement(complement());
        address.setNeighborhood(neighborhood());
        address.setCep(cep());
        return address;
    }
}


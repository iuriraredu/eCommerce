package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.Address;
import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
        Long id,

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "Number is required")
        String number,

        String complement,

        @NotBlank(message = "Neighborhood is required")
        String neighborhood,

        @NotBlank(message = "Zip code is required")
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

    // id is always ignored here on purpose: a new address should never be born with an id coming from the API client.
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

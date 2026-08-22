package br.com.iuriraredu.ecommerce.mapper;

import br.com.iuriraredu.ecommerce.dto.ClientRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ClientResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Client;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, PhoneMapper.class})
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    Client toEntity(ClientRequestDTO dto);

    // update() only ever changed name/email/cpf in the original hand-written logic — addresses
    // and phones are intentionally left untouched here. If they weren't ignored, MapStruct's
    // default null-handling would overwrite the client's existing addresses/phones with null
    // whenever the update request omits them (as every current caller does).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "phones", ignore = true)
    void updateEntityFromDto(ClientRequestDTO dto, @MappingTarget Client client);

    ClientResponseDTO toResponseDTO(Client client);

    // MapStruct maps addresses/phones as flat lists of Address/Phone, but each one still needs
    // its `client` back-reference set for the bidirectional JPA relationship (and for cascade
    // persistence to work) — that's not something a field-by-field mapper can infer on its own.
    @AfterMapping
    default void linkAddressesAndPhonesToClient(@MappingTarget final Client client) {
        if (client.getAddresses() != null) {
            client.getAddresses().forEach(address -> address.setClient(client));
        }
        if (client.getPhones() != null) {
            client.getPhones().forEach(phone -> phone.setClient(client));
        }
    }
}

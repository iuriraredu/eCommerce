package br.com.iuriraredu.ecommerce.mapper;

import br.com.iuriraredu.ecommerce.dto.AddressRequestDTO;
import br.com.iuriraredu.ecommerce.dto.AddressResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    // id and client are never trusted from the request: id is database-generated, and the
    // back-reference to the owning Client is set explicitly by ClientMapper after mapping
    // (see ClientMapper#linkAddressesAndPhonesToClient), not here.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    Address toEntity(AddressRequestDTO dto);

    AddressResponseDTO toResponseDTO(Address address);
}

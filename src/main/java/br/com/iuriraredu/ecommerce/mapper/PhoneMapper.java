package br.com.iuriraredu.ecommerce.mapper;

import br.com.iuriraredu.ecommerce.dto.PhoneRequestDTO;
import br.com.iuriraredu.ecommerce.dto.PhoneResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Phone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhoneMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    Phone toEntity(PhoneRequestDTO dto);

    PhoneResponseDTO toResponseDTO(Phone phone);
}

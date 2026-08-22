package br.com.iuriraredu.ecommerce.mapper;

import br.com.iuriraredu.ecommerce.dto.OrderItemResponseDTO;
import br.com.iuriraredu.ecommerce.dto.OrderResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Order;
import br.com.iuriraredu.ecommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Only covers Order -> OrderResponseDTO (output side). Creating an Order from an OrderRequestDTO
// involves fetching the client/product, checking and decrementing stock, and building the
// address/document snapshots — that's business logic, not a field-by-field conversion, so it
// stays in OrderService.create() instead of being forced into a mapper.
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "clientId", source = "client.id")
    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponseDTO toItemResponseDTO(OrderItem item);
}

package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        LocalDateTime orderDate,
        OrderStatus status,
        Long clientId,
        String clientDocumentSnapshot,
        String deliveryAddressSnapshot,
        List<OrderItemResponseDTO> items
) {
}

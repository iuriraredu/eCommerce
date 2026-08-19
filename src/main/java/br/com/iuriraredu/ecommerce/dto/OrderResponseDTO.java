package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.Order;
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
    public static OrderResponseDTO fromEntity(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getClient().getId(),
                order.getClientDocumentSnapshot(),
                order.getDeliveryAddressSnapshot(),
                order.getItems().stream().map(OrderItemResponseDTO::fromEntity).toList()
        );
    }
}
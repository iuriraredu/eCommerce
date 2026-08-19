package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal soldPrice
) {
    public static OrderItemResponseDTO fromEntity(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getSoldPrice()
        );
    }
}



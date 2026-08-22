package br.com.iuriraredu.ecommerce.dto;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal soldPrice
) {
}

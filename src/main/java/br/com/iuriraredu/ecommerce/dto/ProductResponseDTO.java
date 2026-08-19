package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.Product;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Boolean active
) {
    // Método de fábrica: mantém a lógica de conversão junto do DTO, evitando espalhar mapeamento pelo código.
    public static ProductResponseDTO fromEntity(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getActive()
        );
    }
}
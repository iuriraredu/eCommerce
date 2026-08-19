package br.com.iuriraredu.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(name = "ProductRequestDTO", description = "Dados de entrada para cadastro ou atualização de um produto")
public record ProductRequestDTO(

        @NotBlank(message = "O nome do produto é obrigatório")
        @Schema(description = "Nome do produto", example = "Teclado mecânico", requiredMode = REQUIRED)
        String name,

        @Schema(description = "Descrição do produto", example = "Teclado mecânico ABNT2 com switches vermelhos")
        String description,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
        @Schema(description = "Preço unitário do produto", example = "349.90", requiredMode = REQUIRED)
        BigDecimal price,

        @NotNull(message = "A quantidade em estoque é obrigatória")
        @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa")
        @Schema(description = "Quantidade disponível em estoque", example = "50", requiredMode = REQUIRED)
        Integer stockQuantity,

        @Schema(description = "Indica se o produto está ativo para venda", example = "true")
        Boolean active
) {
}
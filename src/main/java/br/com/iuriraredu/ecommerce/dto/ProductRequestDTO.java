package br.com.iuriraredu.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(name = "ProductRequestDTO", description = "Input data for creating or updating a product")
public record ProductRequestDTO(

        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
        @Schema(description = "Product name", example = "Mechanical keyboard", requiredMode = REQUIRED)
        String name,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        @Schema(description = "Product description", example = "ABNT2 mechanical keyboard with red switches")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        @Schema(description = "Product unit price", example = "349.90", requiredMode = REQUIRED)
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock quantity cannot be negative")
        @Schema(description = "Quantity available in stock", example = "50", requiredMode = REQUIRED)
        Integer stockQuantity,

        @Schema(description = "Indicates whether the product is active for sale", example = "true")
        Boolean active
) {
}

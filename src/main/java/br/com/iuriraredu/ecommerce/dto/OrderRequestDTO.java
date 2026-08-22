package br.com.iuriraredu.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(

        @NotNull(message = "Client is required")
        Long clientId,

        @NotNull(message = "Delivery address is required")
        Long deliveryAddressId,

        @NotEmpty(message = "The order must have at least one item")
        @Valid
        List<OrderItemRequestDTO> items
) {
}

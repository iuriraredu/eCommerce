package br.com.iuriraredu.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(

        @NotNull(message = "O cliente é obrigatório")
        Long clientId,

        @NotNull(message = "O endereço de entrega é obrigatório")
        Long deliveryAddressId,

        @NotEmpty(message = "O pedido precisa ter ao menos um item")
        @Valid
        List<OrderItemRequestDTO> items
) {
}


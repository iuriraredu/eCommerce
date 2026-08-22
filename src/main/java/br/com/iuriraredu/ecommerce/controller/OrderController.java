package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.dto.OrderRequestDTO;
import br.com.iuriraredu.ecommerce.dto.OrderResponseDTO;
import br.com.iuriraredu.ecommerce.entity.enums.OrderStatus;
import br.com.iuriraredu.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Product order management (Create, List and Update orders).")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Create new order",
            description = "Registers a new order in the system using the data provided in the request body and returns the created order with a 201 Created status."
    )
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data provided for order creation")
    @ApiResponse(responseCode = "404", description = "Client, address, or product provided was not found")
    public ResponseEntity<OrderResponseDTO> create(@RequestBody @Valid final OrderRequestDTO dto) {
        OrderResponseDTO createdOrder = orderService.create(dto);
        return ResponseEntity.status(CREATED).body(createdOrder);
    }

    @GetMapping
    @Operation(
            summary = "List all orders",
            description = "Returns a list containing all orders placed in the system."
    )
    @ApiResponse(responseCode = "200", description = "List of orders returned successfully")
    public List<OrderResponseDTO> getAll() {
        return orderService.getAll();
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Update order status",
            description = "Partially updates the status of a specific order based on the ID provided in the URL and the new status sent in the request body."
    )
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status or incorrect data")
    @ApiResponse(responseCode = "404", description = "Order not found for the given ID")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable final Long id, @RequestBody final OrderStatus newStatus) {
        return ResponseEntity.ok(orderService.updateStatus(id, newStatus));
    }
}

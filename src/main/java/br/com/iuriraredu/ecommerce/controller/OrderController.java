package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.entity.Order;
import br.com.iuriraredu.ecommerce.entity.enums.OrderStatus;
import br.com.iuriraredu.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pedido", description = "Gerenciamento de pedidos de produtos (Criar, Listar e Atualizar pedidos).")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Criar novo pedido",
            description = "Registra um novo pedido no sistema com base nos dados fornecidos no corpo da requisição e retorna o pedido criado com status 201 Created."
    )
    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para a criação do pedido")
    public ResponseEntity<Order> create(@RequestBody Order order) {
        Order createdOrder = orderService.create(order);
        return ResponseEntity.status(CREATED).body(createdOrder);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os pedidos",
            description = "Retorna uma lista contendo todos os pedidos realizados no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    public List<Order> getAll() {
        return orderService.getAll();
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status do pedido",
            description = "Atualiza parcialmente o status de um pedido específico com base no ID fornecido na URL e no novo status enviado no corpo da requisição."
    )
    @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Status inválido ou dados incorretos")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado para o ID informado")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestBody OrderStatus newStatus) {
        return ResponseEntity.ok(orderService.updateStatus(id, newStatus));
    }
}
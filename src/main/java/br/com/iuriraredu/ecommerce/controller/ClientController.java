package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;

    @PostMapping
    @Operation(
            summary = "Cadastrar novo cliente",
            description = "Cadastra um novo cliente no sistema utilizando os dados fornecidos no corpo da requisição e retorna o cliente criado com status 201 Created."
    )
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para o cadastro")
    public ResponseEntity<Client> create(@RequestBody Client client) {
        Client savedClient = service.create(client);
        return ResponseEntity.status(CREATED).body(savedClient);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os clientes",
            description = "Retorna uma lista contendo todos os clientes cadastrados no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public List<Client> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna os detalhes de um cliente específico com base no ID informado na URL."
    )
    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID informado")
    public ResponseEntity<Client> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar cliente",
            description = "Atualiza os dados cadastrais de um cliente existente com base no ID informado e nos novos dados fornecidos no corpo da requisição."
    )
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para a atualização")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID informado")
    public ResponseEntity<Client> update(@PathVariable Long id, @RequestBody Client client) {
        return ResponseEntity.ok(service.update(id, client));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar cliente",
            description = "Remove um cliente do sistema com base no ID informado na URL."
    )
    @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID informado")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.dto.ClientRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ClientResponseDTO;
import br.com.iuriraredu.ecommerce.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Cliente", description = "Gerenciamento de clientes (Cadastra, Busca, Atualiza e Deleta clientes).")
public class ClientController {
    private final ClientService service; // Olhando aqui fica claro que "service" é uma service de clientes, mas em outras partes se eu olho, não é possível saber se é service de cliente, service de supermercado, service de loja etc. Precisa renomear.

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Cadastrar novo cliente",
            description = "Cadastra um novo cliente no sistema utilizando os dados fornecidos no corpo da requisição e retorna o cliente criado com status 201 Created."
    )
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para o cadastro")
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid ClientRequestDTO dto) { // O que é "dto"??? Precisa ser mais específico no nome do parâmetro
        ClientResponseDTO savedClient = service.create(dto); // O método create cria o que? Cria um caminhão? Precisa renomear.
        return ResponseEntity.status(CREATED).body(savedClient);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE) // Não precisa do consumes/produces, fica redundante
    @Operation(
            summary = "Listar todos os clientes",
            description = "Retorna uma lista contendo todos os clientes cadastrados no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public List<ClientResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE) // Não precisa do consumes/produces, fica redundante
    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna os detalhes de um cliente específico com base no ID informado na URL."
    )
    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID informado")
    public ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id) { // O que é "id", "id" de quem?? id do cliente? id do supermercado?
        return ResponseEntity.ok(service.findById(id)); // O método findById procura o que? Procura um caminhão? Precisa renomear.
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE) // Não precisa do consumes/produces, fica redundante
    @Operation(
            summary = "Atualizar cliente",
            description = "Atualiza os dados cadastrais de um cliente existente com base no ID informado e nos novos dados fornecidos no corpo da requisição."
    )
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para a atualização")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID informado")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id,  // O que é "id", "id" de quem?? id do cliente? id do supermercado?
    @RequestBody @Valid ClientRequestDTO dto) { // O que é "dto"??? Precisa ser mais específico no nome do parâmetro
        return ResponseEntity.ok(service.update(id, dto)); // O método create atualiza o que por qual parâmetro? Atualiza um caminhão pela placa? Precisa renomear.
    }

    @DeleteMapping(value = "/{id}")
    @Operation(
            summary = "Deletar cliente",
            description = "Remove um cliente do sistema com base no ID informado na URL."
    )
    @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID informado")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id); // O método delete deleta o que por qual parâmetro? Deleta um caminhão pela placa? Precisa renomear.
        return ResponseEntity.noContent().build();
    }
}
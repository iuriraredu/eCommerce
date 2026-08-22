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

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Client management (Create, Read, Update and Delete clients).")
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    @Operation(
            summary = "Register a new client",
            description = "Registers a new client using the data provided in the request body and returns the created client with a 201 Created status."
    )
    @ApiResponse(responseCode = "201", description = "Client registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data provided for registration")
    public ResponseEntity<ClientResponseDTO> createClient(
            @RequestBody @Valid final ClientRequestDTO clientRequestDto) {
        ClientResponseDTO savedClient = clientService.createClient(clientRequestDto);
        return ResponseEntity.status(CREATED).body(savedClient);
    }

    @GetMapping
    @Operation(
            summary = "List all clients",
            description = "Returns a list containing all clients registered in the system."
    )
    @ApiResponse(responseCode = "200", description = "List of clients returned successfully")
    public List<ClientResponseDTO> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Find client by ID",
            description = "Returns the details of a specific client based on the ID provided in the URL."
    )
    @ApiResponse(responseCode = "200", description = "Client found successfully")
    @ApiResponse(responseCode = "404", description = "Client not found for the given ID")
    public ResponseEntity<ClientResponseDTO> findClientById(@PathVariable final Long idClient) {
        return ResponseEntity.ok(clientService.findClientById(idClient));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update client",
            description = "Updates the registration data of an existing client based on the given ID and the new data provided in the request body."
    )
    @ApiResponse(responseCode = "200", description = "Client updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data provided for the update")
    @ApiResponse(responseCode = "404", description = "Client not found for the given ID")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable final Long idCliente,
            @RequestBody @Valid final ClientRequestDTO clientRequestDTO) {
        return ResponseEntity.ok(clientService.updateClient(idCliente, clientRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete client",
            description = "Removes a client from the system based on the ID provided in the URL."
    )
    @ApiResponse(responseCode = "204", description = "Client deleted successfully")
    @ApiResponse(responseCode = "404", description = "Client not found for the given ID")
    public ResponseEntity<Void> deleteClient(@PathVariable final Long idClient) {
        clientService.deleteClient(idClient);
        return ResponseEntity.noContent().build();
    }
}

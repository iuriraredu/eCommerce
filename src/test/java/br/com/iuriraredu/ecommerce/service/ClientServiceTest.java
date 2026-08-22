package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.ClientRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ClientResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.mapper.ClientMapper;
import br.com.iuriraredu.ecommerce.repository.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Tests ClientService's own logic (what it calls, in what order, how it reacts to not-found).
// The actual DTO <-> entity conversion — including the bidirectional address/phone linking —
// now lives in ClientMapper and is covered separately in ClientMapperTest, against the real
// generated implementation instead of a mock.
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    @Test
    @DisplayName("Should create client successfully")
    void createClientSuccess() {
        // Arrange
        final ClientRequestDTO dto = new ClientRequestDTO("Iuri Ramos", "iuri@teste.com", "123.456.789-00", null, null);
        final Client mappedClient = new Client();
        final Client savedClient = new Client();
        savedClient.setId(1L);
        final ClientResponseDTO expectedResponse = new ClientResponseDTO(1L, "Iuri Ramos", "iuri@teste.com", "123.456.789-00", List.of(), List.of());

        when(clientMapper.toEntity(dto)).thenReturn(mappedClient);
        when(clientRepository.save(mappedClient)).thenReturn(savedClient);
        when(clientMapper.toResponseDTO(savedClient)).thenReturn(expectedResponse);

        // Act
        final ClientResponseDTO createdClient = clientService.createClient(dto);

        // Assert
        assertNotNull(createdClient);
        assertEquals("Iuri Ramos", createdClient.name());
        verify(clientMapper, times(1)).toEntity(dto);
        verify(clientRepository, times(1)).save(mappedClient);
    }

    @Test
    @DisplayName("Should return all clients")
    void getAllClientsSuccess() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(List.of(new Client(), new Client()));
        when(clientMapper.toResponseDTO(any(Client.class)))
                .thenReturn(new ClientResponseDTO(1L, "A", "a@a.com", "cpf", List.of(), List.of()));

        // Act
        final List<ClientResponseDTO> result = clientService.getAllClients();

        // Assert
        assertEquals(2, result.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find client by id when client exists")
    void findByIdSuccess() {
        // Arrange
        final Long clientId = 1L;
        final Client client = new Client();
        client.setId(clientId);
        final ClientResponseDTO expectedResponse = new ClientResponseDTO(clientId, "Name", "email", "cpf", List.of(), List.of());

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientMapper.toResponseDTO(client)).thenReturn(expectedResponse);

        // Act
        final ClientResponseDTO result = clientService.findClientById(clientId);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.id());
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when find client by id does not exist")
    void findByIdNotFound() {
        // Arrange
        final Long clientId = 99L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                clientService.findClientById(clientId));

        assertEquals("Client not found with id: 99", exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    @DisplayName("Should update client when client exists")
    void updateClientSuccess() {
        // Arrange
        final Long clientId = 1L;
        final Client existingClient = new Client();
        existingClient.setId(clientId);

        final ClientRequestDTO updatedData = new ClientRequestDTO("Nome Novo", "novo@teste.com", "123.456.789-00", null, null);
        final ClientResponseDTO expectedResponse = new ClientResponseDTO(clientId, "Nome Novo", "novo@teste.com", "123.456.789-00", List.of(), List.of());

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(existingClient)).thenReturn(existingClient);
        when(clientMapper.toResponseDTO(existingClient)).thenReturn(expectedResponse);

        // Act
        final ClientResponseDTO result = clientService.updateClient(clientId, updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("Nome Novo", result.name());
        verify(clientRepository, times(1)).findById(clientId);
        verify(clientMapper, times(1)).updateEntityFromDto(updatedData, existingClient);
        verify(clientRepository, times(1)).save(existingClient);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to update non-existent client")
    void updateClientNotFound() {
        // Arrange
        final Long clientId = 99L;
        final ClientRequestDTO updatedData = new ClientRequestDTO("Nome", "email@teste.com", "000.000.000-00", null, null);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                clientService.updateClient(clientId, updatedData));

        assertEquals("Client not found with id: 99", exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete client when client exists")
    void deleteClientSuccess() {
        // Arrange
        final Long clientId = 1L;
        when(clientRepository.existsById(clientId)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(clientId);

        // Act & Assert
        assertDoesNotThrow(() -> clientService.deleteClient(clientId));

        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, times(1)).deleteById(clientId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete non-existent client")
    void deleteClientNotFound() {
        // Arrange
        final Long clientId = 99L;
        when(clientRepository.existsById(clientId)).thenReturn(false);

        // Act & Assert
        final ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                clientService.deleteClient(clientId));

        assertEquals("Client not found with id: 99", exception.getMessage());
        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, never()).deleteById(any());
    }
}

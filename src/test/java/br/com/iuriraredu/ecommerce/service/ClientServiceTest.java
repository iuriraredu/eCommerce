package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Address;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.entity.Phone;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
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

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    @DisplayName("Should create client successfully and associate addresses and phones")
    void createClientSuccess() {
        // Arrange
        Client client = new Client();
        client.setName("Iuri Ramos");

        Address address = new Address();
        address.setStreet("Rua Teste");
        client.setAddresses(List.of(address));

        Phone phone = new Phone();
        phone.setNumber("11999999999");
        client.setPhones(List.of(phone));

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Act
        Client createdClient = clientService.create(client);

        // Assert
        assertNotNull(createdClient);
        assertEquals(client, address.getClient());
        assertEquals(client, phone.getClient());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    @DisplayName("Should return all clients")
    void getAllClientsSuccess() {
        // Arrange
        List<Client> clients = List.of(new Client(), new Client());
        when(clientRepository.findAll()).thenReturn(clients);

        // Act
        List<Client> result = clientService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find client by id when client exists")
    void findByIdSuccess() {
        // Arrange
        Long clientId = 1L;
        Client client = new Client();
        client.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act
        Client result = clientService.findById(clientId);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getId());
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when find client by id does not exist")
    void findByIdNotFound() {
        // Arrange
        Long clientId = 99L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clientService.findById(clientId);
        });

        assertEquals("Client not found with id: 99", exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    @DisplayName("Should update client when client exists")
    void updateClientSuccess() {
        // Arrange
        Long clientId = 1L;
        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setName("Nome Antigo");

        Client updatedData = new Client();
        updatedData.setName("Nome Novo");
        updatedData.setCpf("123.456.789-00");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // Act
        Client result = clientService.update(clientId, updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("Nome Novo", result.getName());
        verify(clientRepository, times(1)).findById(clientId);
        verify(clientRepository, times(1)).save(existingClient);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to update non-existent client")
    void updateClientNotFound() {
        // Arrange
        Long clientId = 99L;
        Client updatedData = new Client();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clientService.update(clientId, updatedData);
        });

        assertEquals("Client not found with id: 99", exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete client when client exists")
    void deleteClientSuccess() {
        // Arrange
        Long clientId = 1L;
        when(clientRepository.existsById(clientId)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(clientId);

        // Act & Assert
        assertDoesNotThrow(() -> clientService.delete(clientId));

        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, times(1)).deleteById(clientId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete non-existent client")
    void deleteClientNotFound() {
        // Arrange
        Long clientId = 99L;
        when(clientRepository.existsById(clientId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clientService.delete(clientId);
        });

        assertEquals("Client not found with id: 99", exception.getMessage());
        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, never()).deleteById(any());
    }
}
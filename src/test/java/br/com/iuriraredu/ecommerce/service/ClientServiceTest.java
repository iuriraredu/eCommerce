package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Address;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.entity.Phone;
import br.com.iuriraredu.ecommerce.repository.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        Optional<Client> result = clientService.findById(clientId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(clientId, result.get().getId());
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

        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(clientRepository.save(any(Client.class))).thenReturn(updatedData);

        // Act
        Optional<Client> result = clientService.update(clientId, updatedData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(clientId, result.get().getId());
        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, times(1)).save(updatedData);
    }

    @Test
    @DisplayName("Should return empty when trying to update non-existent client")
    void updateClientNotFound() {
        // Arrange
        Long clientId = 99L;
        Client updatedData = new Client();

        when(clientRepository.existsById(clientId)).thenReturn(false);

        // Act
        Optional<Client> result = clientService.update(clientId, updatedData);

        // Assert
        assertTrue(result.isEmpty());
        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete client when client exists")
    void deleteClientSuccess() {
        // Arrange
        Long clientId = 1L;
        when(clientRepository.existsById(clientId)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(clientId);

        // Act
        boolean result = clientService.delete(clientId);

        // Assert
        assertTrue(result);
        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, times(1)).deleteById(clientId);
    }

    @Test
    @DisplayName("Should return false when trying to delete non-existent client")
    void deleteClientNotFound() {
        // Arrange
        Long clientId = 99L;
        when(clientRepository.existsById(clientId)).thenReturn(false);

        // Act
        boolean result = clientService.delete(clientId);

        // Assert
        assertFalse(result);
        verify(clientRepository, times(1)).existsById(clientId);
        verify(clientRepository, never()).deleteById(any());
    }
}


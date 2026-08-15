package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client create(Client client) {
        if (client.getAddresses() != null)
            client.getAddresses().forEach(address -> address.setClient(client));

        if (client.getPhones() != null)
            client.getPhones().forEach(phone -> phone.setClient(client));

        return clientRepository.save(client);
    }

    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    public Client update(Long id, Client updatedClient) {
        Client client = findById(id);
        client.setName(updatedClient.getName());
        client.setCpf(updatedClient.getCpf());
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
}
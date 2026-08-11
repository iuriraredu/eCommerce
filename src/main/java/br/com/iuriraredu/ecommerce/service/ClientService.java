package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Client;
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

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> update(Long id, Client newClient) {
        if (!clientRepository.existsById(id)) return Optional.empty();
        newClient.setId(id);
        return Optional.of(clientRepository.save(newClient));
    }

    public boolean delete(Long id) {
        if (!clientRepository.existsById(id)) return false;
        clientRepository.deleteById(id);
        return true;
    }
}
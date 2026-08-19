package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.ClientRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ClientResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Address;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.entity.Phone;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    @CacheEvict(value = "clients", allEntries = true) // Cache do spring em geral é mais lento do que fazer cache na mão.
    public ClientResponseDTO create(ClientRequestDTO dto) {
        Client client = new Client();
        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setCpf(dto.cpf());

        // Muita lógica de mapeamento na service que não deveria estar aqui.
        if (dto.addresses() != null) {
            List<Address> addresses = dto.addresses().stream().map(a -> {
                Address address = a.toEntity();
                address.setClient(client);
                return address;
            }).toList();
            client.setAddresses(addresses);
        }

        if (dto.phones() != null) {
            List<Phone> phones = dto.phones().stream().map(p -> {
                Phone phone = p.toEntity();
                phone.setClient(client);
                return phone;
            }).toList();
            client.setPhones(phones);
        }

        return ClientResponseDTO.fromEntity(clientRepository.save(client));
    }

    @Cacheable(value = "clients")
    public List<ClientResponseDTO> getAll() {
        return clientRepository.findAll().stream()
                .map(ClientResponseDTO::fromEntity)
                .toList();
    }

    @Cacheable(value = "clients", key = "#id")
    public ClientResponseDTO findById(Long id) {
        return ClientResponseDTO.fromEntity(findEntityById(id));
    }

    @Transactional // Você sabe o que o @Transactional faz? Se você tomar uma checked exception aqui, provavelmente esse cara não vai funcionar.
    @CacheEvict(value = "clients", allEntries = true)
    public ClientResponseDTO update(Long id, ClientRequestDTO dto) {
        Client client = findEntityById(id);
        client.setName(dto.name()); // Por que está mapeando os campos aqui quando você tem DTOs com mappers imbutidos?
        client.setEmail(dto.email());
        client.setCpf(dto.cpf());
        return ClientResponseDTO.fromEntity(clientRepository.save(client));
    }

    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    // Uso interno (ex.: OrderService) quando é preciso a entidade gerenciada, não o DTO.
    Client findEntityById(Long id) { // Se esse cara é pra uso interno, cadê o modificador "private"?
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }
}
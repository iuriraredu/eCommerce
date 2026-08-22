package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.ClientRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ClientResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.mapper.ClientMapper;
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
    private final ClientMapper clientMapper;

    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public ClientResponseDTO createClient(final ClientRequestDTO dto) {
        final Client client = clientMapper.toEntity(dto);
        return clientMapper.toResponseDTO(clientRepository.save(client));
    }

    @Cacheable(value = "clients")
    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponseDTO)
                .toList();
    }

    @Cacheable(value = "clients", key = "#id")
    public ClientResponseDTO findClientById(final Long id) {
        return clientMapper.toResponseDTO(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public ClientResponseDTO updateClient(final Long id, final ClientRequestDTO dto) {
        final Client client = findEntityById(id);
        clientMapper.updateEntityFromDto(dto, client);
        return clientMapper.toResponseDTO(clientRepository.save(client));
    }

    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public void deleteClient(final Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    // Internal use only: no other class calls this method, so there's no reason for it to be
    // more visible than it needs to be (encapsulation).
    private Client findEntityById(final Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }
}

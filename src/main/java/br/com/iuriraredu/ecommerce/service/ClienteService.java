package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Cliente;
import br.com.iuriraredu.ecommerce.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    public Cliente criar(Cliente cliente) {
        if (cliente.getEnderecos() != null)
            cliente.getEnderecos().forEach(endereco -> endereco.setCliente(cliente));

        if (cliente.getTelefones() != null)
            cliente.getTelefones().forEach(telefone -> telefone.setCliente(cliente));

        return repository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<Cliente> atualizar(Long id, Cliente clienteAtualizado) {
        if (!repository.existsById(id)) return Optional.empty();
        clienteAtualizado.setId(id);
        return Optional.of(repository.save(clienteAtualizado));
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}


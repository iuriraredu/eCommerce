package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Produto;
import br.com.iuriraredu.ecommerce.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;

    public Produto criar(Produto produto) {
        return repository.save(produto);
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<Produto> atualizar(Long id, Produto produtoAtualizado) {
        if (!repository.existsById(id)) return Optional.empty();
        produtoAtualizado.setId(id);
        return Optional.of(repository.save(produtoAtualizado));
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}

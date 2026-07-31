package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.entity.Produto;
import br.com.iuriraredu.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController // Diz que esta classe vai responder requisições da web (e devolver JSON).
@RequestMapping("/produtos") // Define que a URL base para esta classe será localhost:8080/produtos
public class ProdutoController {

    @Autowired // Injeta (traz) o nosso ProdutoRepository para usarmos aqui dentro.
    private ProdutoRepository repository;

    // 1. CADASTRAR PRODUTO (POST localhost:8080/produtos)
    @PostMapping
    public Produto criar(@RequestBody Produto produto) {
        // @RequestBody avisa que os dados do produto virão no "corpo" da requisição em formato JSON.
        return repository.save(produto);
    }

    // 2. LISTAR TODOS (GET localhost:8080/produtos)
    @GetMapping
    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    // 3. BUSCAR POR ID (GET localhost:8080/produtos/1)
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        // @PathVariable pega o número "1" da URL e joga na variável 'id'.
        return repository.findById(id)
                .map(produto -> ResponseEntity.ok(produto)) // Se achar, devolve 200 OK.
                .orElse(ResponseEntity.notFound().build()); // Se não achar, devolve 404 Not Found.
    }

    // 4. ATUALIZAR (PUT localhost:8080/produtos/1)
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produtoAtualizado) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        produtoAtualizado.setId(id); // Trava o ID para garantir que vamos sobrescrever o produto certo.
        Produto salvo = repository.save(produtoAtualizado);
        return ResponseEntity.ok(salvo);
    }

    // 5. DELETAR (DELETE localhost:8080/produtos/1)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content (Deletado com sucesso, nada a retornar).
    }
}

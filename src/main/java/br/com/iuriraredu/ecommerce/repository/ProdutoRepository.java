package br.com.iuriraredu.ecommerce.repository;

import br.com.iuriraredu.ecommerce.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Opcional, mas boa prática para dizer ao Spring que esta interface lida com dados.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Deixe vazio!
    // Como estamos herdando o JpaRepository e passamos a classe <Produto> e o tipo do ID <Long>,
    // o Spring já programou secretamente comandos como save(), findAll(), findById() e deleteById() para nós.
}



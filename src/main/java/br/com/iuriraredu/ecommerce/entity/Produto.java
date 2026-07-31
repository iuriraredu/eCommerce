package br.com.iuriraredu.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.*;

@Data // O Lombok cria os Getters, Setters e o toString automaticamente, limpando o código.
@Entity // Avisa ao banco de dados: "Crie uma tabela para esta classe".
public class Produto {
    @Id // Diz que este campo é a Chave Primária (identificador único).
    @GeneratedValue(strategy = IDENTITY) // O banco de dados vai criar os IDs em sequência (1, 2, 3...) sozinho.
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
}

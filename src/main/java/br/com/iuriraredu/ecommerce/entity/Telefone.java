package br.com.iuriraredu.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class Telefone {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String numero;

    // Relacionamento: Muitos telefones podem pertencer a Um cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}


package br.com.iuriraredu.ecommerce.entity;

import br.com.iuriraredu.ecommerce.entity.emuns.StatusPedido;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.iuriraredu.ecommerce.entity.emuns.StatusPedido.AGUARDANDO_PAGAMENTO;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private LocalDateTime dataPedido = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private StatusPedido status = AGUARDANDO_PAGAMENTO;

    // Fotografia dos dados do cliente no momento da compra
    private String documentoClienteSnapshot;
    private String enderecoEntregaSnapshot;

    // TRUQUE DE MESTRE: @Transient faz o JPA ignorar esse campo na hora de criar a tabela.
    // Usamos isso só para receber o ID do endereço pelo JSON do Postman/Front-end!
    @Transient
    private Long idEnderecoEntrega;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = ALL)
    private List<ItemPedido> itens;
}


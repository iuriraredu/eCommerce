package br.com.iuriraredu.ecommerce.entity;

import br.com.iuriraredu.ecommerce.entity.enums.TipoPagamento;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class Pagamento {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private BigDecimal valorPago;
    private LocalDateTime dataConfirmacao;

    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}

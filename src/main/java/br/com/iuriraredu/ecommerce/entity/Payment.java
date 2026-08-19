package br.com.iuriraredu.ecommerce.entity;

import br.com.iuriraredu.ecommerce.entity.enums.PaymentType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private BigDecimal paidAmount;
    private LocalDateTime confirmationDate;

    @Enumerated(STRING)
    private PaymentType paymentType;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}

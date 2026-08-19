package br.com.iuriraredu.ecommerce.entity;

import br.com.iuriraredu.ecommerce.entity.enums.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.iuriraredu.ecommerce.entity.enums.OrderStatus.WAITING_FOR_PAYMENT;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "items")
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(STRING)
    private OrderStatus status = WAITING_FOR_PAYMENT;

    private String clientDocumentSnapshot;
    private String deliveryAddressSnapshot;

    @Transient
    private Long deliveryAddressId;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderItem> items;
}


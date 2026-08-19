package br.com.iuriraredu.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "client")
@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String number;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}


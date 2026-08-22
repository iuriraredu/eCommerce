package br.com.iuriraredu.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

// We swapped @Data for @Getter/@Setter + equals/hashCode based on id only:
// with a bidirectional relationship (addresses, phones), @Data's "everything" equals/hashCode/toString
// enters infinite recursion (Client -> addresses -> Address.client -> Client -> ...).
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"addresses", "phones"})
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String cpf;

    @OneToMany(mappedBy = "client", cascade = ALL, orphanRemoval = true)
    private List<Address> addresses;

    @OneToMany(mappedBy = "client", cascade = ALL, orphanRemoval = true)
    private List<Phone> phones;
}
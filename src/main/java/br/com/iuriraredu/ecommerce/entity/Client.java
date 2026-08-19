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

@Getter
@Setter
@EqualsAndHashCode(of = "id") // Tira esse cara e faz manualmente, seguindo ali como o plugin JPA Buddy cria para você.
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
    private List<Address> addresses; // Talvez fosse interessante ser um Set<Address> em vez de List<Address>, mas isso depende da regra de negócio. O mesmo se aplica para outros campos de List<...>. 

    @OneToMany(mappedBy = "client", cascade = ALL, orphanRemoval = true)
    private List<Phone> phones;
}
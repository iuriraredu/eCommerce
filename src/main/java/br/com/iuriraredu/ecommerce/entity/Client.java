package br.com.iuriraredu.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
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
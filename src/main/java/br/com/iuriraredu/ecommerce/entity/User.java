package br.com.iuriraredu.ecommerce.entity;

import br.com.iuriraredu.ecommerce.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static br.com.iuriraredu.ecommerce.entity.enums.UserRole.USER;
import static jakarta.persistence.GenerationType.IDENTITY;

// Pure persistence entity: knows nothing about Spring Security. See UserDetailsImpl for the
// adapter that bridges this entity to the UserDetails contract.
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "password") // never let the password hash leak into logs/debug via toString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = USER;
}

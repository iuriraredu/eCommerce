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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static br.com.iuriraredu.ecommerce.entity.enums.UserRole.ADMIN;
import static br.com.iuriraredu.ecommerce.entity.enums.UserRole.USER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "password")
@Entity
@Table(name = "users")
public class User implements UserDetails { // Seu banco de dados está dependente do Spring, se você trocar pro Quarkus vai dar erro nessa classe sem precisar. É interessante ter uma classe diferente que implemente o UserDetails e deixar a classe atual User.java apenas como entidade de banco de dados.
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false) // Cadê o limite de caracteres?
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (this.role == ADMIN)
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")) // Isso aqui não deveria ser preenchido pelo código, mas sim por uma relação entre usuário e papéis em uma tabela do banco de dados. Se for preencher pelo código, pelo menus utilize a ENUM que você tem para isso.
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
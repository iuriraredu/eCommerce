package br.com.iuriraredu.ecommerce.entity;

import br.com.iuriraredu.ecommerce.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static br.com.iuriraredu.ecommerce.entity.enums.UserRole.ADMIN;
import static br.com.iuriraredu.ecommerce.entity.enums.UserRole.USER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // O login precisa ser único no banco (não podem existir dois iguais)
    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = USER;

    // --- MÉTODOS OBRIGATÓRIOS DO SPRING SECURITY ---

    // Define qual o nível de acesso (role) desse usuário
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (this.role == ADMIN)
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // O Spring precisa saber qual campo é a "senha"
    @Override
    public String getPassword() {
        return this.password;
    }

    // O Spring precisa saber qual campo é o "login"
    @Override
    public String getUsername() {
        return this.login;
    }

    // As validações abaixo bloqueiam a conta se quisermos. Por padrão, deixamos tudo 'true' (ativo).
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

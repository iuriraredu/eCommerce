package br.com.iuriraredu.ecommerce.security;

import br.com.iuriraredu.ecommerce.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static br.com.iuriraredu.ecommerce.entity.enums.UserRole.ADMIN;

// Adapter between our persistence entity (User) and the contract Spring Security expects
// (UserDetails). The entity stays focused only on what the database needs to know about a user;
// this class is the only place that knows how a User maps onto Spring Security's authentication model.
// If we ever change how roles/authorities are computed, or swap the security framework, this is the
// only class that needs to change — User itself is untouched.
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(final User user) {
        this.user = user;
    }

    // Exposes the wrapped entity for code that needs domain data beyond what UserDetails offers
    // (e.g. the user's database id).
    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (user.getRole() == ADMIN)
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
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

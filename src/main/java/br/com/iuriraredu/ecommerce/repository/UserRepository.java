package br.com.iuriraredu.ecommerce.repository;

import br.com.iuriraredu.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Returns the raw entity now — the repository shouldn't know about Spring Security's
    // UserDetails contract. Whoever calls this (AuthorizationService, SecurityFilter) is
    // responsible for wrapping the result in a UserDetailsImpl when needed.
    User findByLogin(String login);

    // Used only to check for duplicate registration. Generates "SELECT EXISTS(...)" on the
    // database instead of loading the whole row — cheaper than findByLogin() != null when
    // all we need is a boolean.
    boolean existsByLogin(String login);
}

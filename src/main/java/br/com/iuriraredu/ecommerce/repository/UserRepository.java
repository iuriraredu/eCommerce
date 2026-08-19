package br.com.iuriraredu.ecommerce.repository;

import br.com.iuriraredu.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Método que o Spring Security vai usar para consultar se o cara existe na hora do login
    UserDetails findByLogin(String login); // Se você só estar consultando se o usuário existe ou não, faça esse método voltar um boolean e utilize uma query personalizada com CASE para isso.
}

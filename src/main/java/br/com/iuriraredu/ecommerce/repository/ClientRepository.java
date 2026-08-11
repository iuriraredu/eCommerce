package br.com.iuriraredu.ecommerce.repository;

import br.com.iuriraredu.ecommerce.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}

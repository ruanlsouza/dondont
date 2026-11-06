package senai499.com.br.donodont.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import senai499.com.br.donodont.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByCpf(String cpf);
}
package offeria.rfq_service.repository;

import offeria.rfq_service.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}

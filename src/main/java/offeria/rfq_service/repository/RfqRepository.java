package offeria.rfq_service.repository;

import offeria.rfq_service.domain.entity.Rfq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RfqRepository extends JpaRepository<Rfq, UUID> {

    boolean existsByClientIdAndRfqNumberIgnoreCase(
            UUID clientId,
            String rfqNumber
    );
}

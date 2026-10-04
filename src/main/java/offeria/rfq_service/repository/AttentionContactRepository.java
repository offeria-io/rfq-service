package offeria.rfq_service.repository;

import offeria.rfq_service.domain.entity.AttentionContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttentionContactRepository
        extends JpaRepository<AttentionContact, UUID> {

    List<AttentionContact> findAllByClientIdOrderByNameAsc(UUID clientId);

    Optional<AttentionContact> findByIdAndClientId(
            UUID id,
            UUID clientId
    );
}

package offeria.rfq_service.repository;

import offeria.rfq_service.domain.entity.Rfq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for RFQ Entity operations.
 */
@Repository
public interface RfqRepository extends JpaRepository<Rfq, UUID> {

    // Find the latest RFQ created to determine the next offer number
    @Query("SELECT r FROM Rfq r ORDER BY r.createdAt DESC LIMIT 1")
    Optional<Rfq> findLatestRfq();
}

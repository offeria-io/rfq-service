package offeria.rfq_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RFQ (Request for Quotation) Entity
 * Represents the database table for RFQs.
 */
@Entity
@Table(name = "rfqs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rfq {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The automatically generated offer number
    @Column(name = "offer_number", nullable = false, unique = true)
    private String offerNumber;

    // The title or description of the RFQ
    @Column(nullable = false)
    private String title;

    // Client/Customer identifier
    @Column(name = "client_name", nullable = false)
    private String clientName;

    // The structured folder name for file storage organization
    @Column(name = "folder_name", nullable = false)
    private String folderName;

    // Status of the RFQ (e.g., PENDING, COMPLETED, CANCELLED)
    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

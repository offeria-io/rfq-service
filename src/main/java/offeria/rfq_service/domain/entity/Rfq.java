package offeria.rfq_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "rfqs",
        indexes = {
                @Index(
                        name = "idx_rfqs_client_id",
                        columnList = "client_id"
                ),
                @Index(
                        name = "idx_rfqs_project_id",
                        columnList = "project_id"
                ),
                @Index(
                        name = "idx_rfqs_contract_id",
                        columnList = "contract_id"
                ),
                @Index(
                        name = "idx_rfqs_work_location_id",
                        columnList = "work_location_id"
                ),
                @Index(
                        name = "idx_rfqs_attention_contact_id",
                        columnList = "attention_contact_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rfq {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "offer_number",
            nullable = false,
            unique = true,
            length = 100
    )
    private String offerNumber;

    @Column(
            name = "rfq_number",
            length = 150
    )
    private String rfqNumber;

    @Column(nullable = false, length = 255)
    private String title;

    /*
     * Legacy field retained temporarily so existing RFQ rows can remain
     * readable during the P03 migration. New RFQs use the Client relation.
     */
    @Column(name = "client_name", nullable = false, length = 255)
    private String clientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_location_id")
    private WorkLocation workLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attention_contact_id")
    private AttentionContact attentionContact;

    @Column(
            name = "folder_name",
            nullable = false,
            length = 500
    )
    private String folderName;

    @Column(nullable = false, length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

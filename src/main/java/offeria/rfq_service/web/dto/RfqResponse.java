package offeria.rfq_service.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for RFQ information.
 * Encapsulates the data returned to the API client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfqResponse {
    private UUID id;
    private String offerNumber;
    private String title;
    private String clientName;
    private String folderName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

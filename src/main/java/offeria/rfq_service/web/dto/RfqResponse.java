package offeria.rfq_service.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfqResponse {

    private UUID id;

    private String rfqNumber;

    private String offerNumber;

    private String title;

    private UUID clientId;
    private String clientCode;
    private String clientName;

    private UUID projectId;
    private String projectCode;
    private String projectName;

    private UUID contractId;
    private String contractCode;

    private UUID workLocationId;
    private String workLocationCode;

    private UUID attentionContactId;
    private String attentionContactName;

    private String folderName;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

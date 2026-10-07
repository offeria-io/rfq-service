package offeria.rfq_service.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfqRequest {

    @NotBlank(message = "RFQ number is required")
    private String rfqNumber;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Client ID is required")
    private UUID clientId;

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    private UUID contractId;

    private UUID workLocationId;

    private UUID attentionContactId;
}

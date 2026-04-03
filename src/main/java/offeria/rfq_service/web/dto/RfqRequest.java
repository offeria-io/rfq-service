package offeria.rfq_service.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for creating a new RFQ.
 * Contains only the necessary fields for creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfqRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Client name is required")
    private String clientName;
}

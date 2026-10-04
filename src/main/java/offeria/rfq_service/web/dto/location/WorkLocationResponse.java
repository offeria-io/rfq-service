package offeria.rfq_service.web.dto.location;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkLocationResponse(
        UUID id,
        UUID projectId,
        String code,
        String nameEn,
        String nameAr,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

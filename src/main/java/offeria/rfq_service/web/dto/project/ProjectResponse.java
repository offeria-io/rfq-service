package offeria.rfq_service.web.dto.project;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        UUID clientId,
        String code,
        String nameEn,
        String nameAr,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

package offeria.rfq_service.web.dto.client;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String code,
        String nameEn,
        String nameAr,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

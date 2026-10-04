package offeria.rfq_service.web.dto.contact;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttentionContactResponse(
        UUID id,
        UUID clientId,
        String name,
        String title,
        String email,
        String phone,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

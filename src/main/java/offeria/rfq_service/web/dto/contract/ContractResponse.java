package offeria.rfq_service.web.dto.contract;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContractResponse(
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

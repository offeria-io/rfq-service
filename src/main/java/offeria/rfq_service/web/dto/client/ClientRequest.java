package offeria.rfq_service.web.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(

        @NotBlank(message = "Client code is required")
        @Size(max = 50, message = "Client code must not exceed 50 characters")
        String code,

        @NotBlank(message = "English client name is required")
        @Size(max = 255, message = "English client name must not exceed 255 characters")
        String nameEn,

        @Size(max = 255, message = "Arabic client name must not exceed 255 characters")
        String nameAr,

        Boolean active
) {
}

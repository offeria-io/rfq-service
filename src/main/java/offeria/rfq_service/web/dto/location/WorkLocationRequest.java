package offeria.rfq_service.web.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkLocationRequest(

        @NotBlank(message = "Work location code is required")
        @Size(max = 100, message = "Work location code must not exceed 100 characters")
        String code,

        @NotBlank(message = "English work location name is required")
        @Size(max = 255, message = "English work location name must not exceed 255 characters")
        String nameEn,

        @Size(max = 255, message = "Arabic work location name must not exceed 255 characters")
        String nameAr,

        Boolean active
) {
}

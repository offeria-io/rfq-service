package offeria.rfq_service.web.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(

        @NotBlank(message = "Project code is required")
        @Size(max = 50, message = "Project code must not exceed 50 characters")
        String code,

        @NotBlank(message = "English project name is required")
        @Size(max = 255, message = "English project name must not exceed 255 characters")
        String nameEn,

        @Size(max = 255, message = "Arabic project name must not exceed 255 characters")
        String nameAr,

        Boolean active
) {
}

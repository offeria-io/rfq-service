package offeria.rfq_service.web.dto.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContractRequest(

        @NotBlank(message = "Contract code is required")
        @Size(max = 100, message = "Contract code must not exceed 100 characters")
        String code,

        @NotBlank(message = "English contract name is required")
        @Size(max = 255, message = "English contract name must not exceed 255 characters")
        String nameEn,

        @Size(max = 255, message = "Arabic contract name must not exceed 255 characters")
        String nameAr,

        Boolean active
) {
}

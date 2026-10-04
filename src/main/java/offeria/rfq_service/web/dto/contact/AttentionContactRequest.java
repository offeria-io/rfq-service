package offeria.rfq_service.web.dto.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AttentionContactRequest(

        @NotBlank(message = "Contact name is required")
        @Size(max = 255)
        String name,

        @Size(max = 255)
        String title,

        @Email(message = "Email must be valid")
        @Size(max = 255)
        String email,

        @Size(max = 100)
        String phone,

        Boolean active
) {
}

package offeria.rfq_service.service.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferFolderNameService {

    private final OfferNumberService offerNumberService;

    public String generate(
            long offerNumber,
            String projectCode,
            String rfqNumber,
            String title
    ) {
        String normalizedProjectCode =
                normalizeRequired(
                        projectCode,
                        "Project code"
                );

        String normalizedRfqNumber =
                normalizeRequired(
                        rfqNumber,
                        "RFQ number"
                );

        String normalizedTitle =
                normalizeRequired(
                        title,
                        "Title"
                );

        return offerNumberService.format(offerNumber)
                + " "
                + normalizedProjectCode
                + " "
                + normalizedRfqNumber
                + " "
                + normalizedTitle;
    }

    private String normalizeRequired(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }

        String normalized = value
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "-")
                .replaceAll("-{2,}", "-")
                .trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }

        return normalized;
    }
}

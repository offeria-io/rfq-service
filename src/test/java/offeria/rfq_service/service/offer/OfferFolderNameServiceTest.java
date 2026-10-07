package offeria.rfq_service.service.offer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfferFolderNameServiceTest {

    private OfferFolderNameService folderNameService;

    @BeforeEach
    void setUp() {
        OfferNumberService offerNumberService =
                new OfferNumberService(null);

        folderNameService =
                new OfferFolderNameService(
                        offerNumberService
                );
    }

    @Test
    void generateShouldProduceCanonicalOfferiaFolderName() {
        String result = folderNameService.generate(
                3198L,
                "BSR-RML-Rumaila GPP CPECC-DIV1",
                "YJRML-2026-0078",
                "MQ IQ04062 Civil engineering consumables"
        );

        assertEquals(
                "Offer#3198 BSR-RML-Rumaila GPP CPECC-DIV1 "
                        + "YJRML-2026-0078 "
                        + "MQ IQ04062 Civil engineering consumables",
                result
        );
    }

    @Test
    void generateShouldTrimAndCollapseWhitespace() {
        String result = folderNameService.generate(
                25L,
                "  BSR-RML   GPP  ",
                "  RFQ-2026-001  ",
                "  Civil   engineering   consumables  "
        );

        assertEquals(
                "Offer#25 BSR-RML GPP RFQ-2026-001 "
                        + "Civil engineering consumables",
                result
        );
    }

    @Test
    void generateShouldReplaceFilesystemInvalidCharacters() {
        String result = folderNameService.generate(
                25L,
                "BSR/RML",
                "RFQ:2026/001",
                "Civil * engineering ? consumables"
        );

        assertEquals(
                "Offer#25 BSR-RML RFQ-2026-001 "
                        + "Civil - engineering - consumables",
                result
        );

        assertFalse(result.contains("/"));
        assertFalse(result.contains(":"));
        assertFalse(result.contains("*"));
        assertFalse(result.contains("?"));
    }

    @Test
    void generateShouldRejectBlankProjectCode() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> folderNameService.generate(
                                25L,
                                "   ",
                                "RFQ-2026-001",
                                "Civil materials"
                        )
                );

        assertEquals(
                "Project code is required",
                exception.getMessage()
        );
    }

    @Test
    void generateShouldRejectBlankRfqNumber() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> folderNameService.generate(
                                25L,
                                "BSR-RML",
                                null,
                                "Civil materials"
                        )
                );

        assertEquals(
                "RFQ number is required",
                exception.getMessage()
        );
    }

    @Test
    void generateShouldRejectBlankTitle() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> folderNameService.generate(
                                25L,
                                "BSR-RML",
                                "RFQ-2026-001",
                                "   "
                        )
                );

        assertEquals(
                "Title is required",
                exception.getMessage()
        );
    }
}

package offeria.rfq_service.web.mapper;

import offeria.rfq_service.domain.entity.Rfq;
import offeria.rfq_service.domain.enums.IntakeSource;
import offeria.rfq_service.domain.enums.IntakeStatus;
import offeria.rfq_service.web.dto.RfqResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class RfqMapperTest {

    private final RfqMapper mapper =
            Mappers.getMapper(RfqMapper.class);

    @Test
    void shouldMapManualIntakeSource() {
        Rfq rfq = Rfq.builder()
                .rfqNumber("RFQ-001")
                .clientName("CPECC")
                .intakeSource(IntakeSource.MANUAL)
                .intakeStatus(IntakeStatus.PENDING_REVIEW)
                .build();

        RfqResponse response = mapper.toResponse(rfq);

        assertEquals(
                IntakeSource.MANUAL,
                response.getIntakeSource()
        );

        assertEquals(
                IntakeStatus.PENDING_REVIEW,
                response.getIntakeStatus()
        );
    }

    @Test
    void shouldPreserveLegacyClientNameWithoutClientRelation() {
        Rfq legacyRfq = Rfq.builder()
                .clientName("Legacy Client")
                .intakeSource(IntakeSource.MANUAL)
                .build();

        RfqResponse response = mapper.toResponse(legacyRfq);

        assertEquals(
                "Legacy Client",
                response.getClientName()
        );

        assertNull(response.getClientId());
    }
}

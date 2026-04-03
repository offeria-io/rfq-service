package offeria.rfq_service.web.mapper;

import offeria.rfq_service.domain.entity.Rfq;
import offeria.rfq_service.web.dto.RfqRequest;
import offeria.rfq_service.web.dto.RfqResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct Mapper for RFQ conversion between Entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RfqMapper {

    // Map creation request to Entity
    Rfq toEntity(RfqRequest request);

    // Map Entity to Response DTO
    RfqResponse toResponse(Rfq rfq);
}

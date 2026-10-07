package offeria.rfq_service.web.mapper;

import offeria.rfq_service.domain.entity.Rfq;
import offeria.rfq_service.web.dto.RfqResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RfqMapper {

    @Mapping(
            target = "clientId",
            source = "client.id"
    )
    @Mapping(
            target = "clientCode",
            source = "client.code"
    )
    @Mapping(
            target = "clientName",
            source = "client.nameEn"
    )
    @Mapping(
            target = "projectId",
            source = "project.id"
    )
    @Mapping(
            target = "projectCode",
            source = "project.code"
    )
    @Mapping(
            target = "projectName",
            source = "project.nameEn"
    )
    @Mapping(
            target = "contractId",
            source = "contract.id"
    )
    @Mapping(
            target = "contractCode",
            source = "contract.code"
    )
    @Mapping(
            target = "workLocationId",
            source = "workLocation.id"
    )
    @Mapping(
            target = "workLocationCode",
            source = "workLocation.code"
    )
    @Mapping(
            target = "attentionContactId",
            source = "attentionContact.id"
    )
    @Mapping(
            target = "attentionContactName",
            source = "attentionContact.name"
    )
    RfqResponse toResponse(Rfq rfq);
}

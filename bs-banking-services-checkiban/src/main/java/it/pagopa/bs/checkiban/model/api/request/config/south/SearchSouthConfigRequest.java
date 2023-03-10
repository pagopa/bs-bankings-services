package it.pagopa.bs.checkiban.model.api.request.config.south;

import java.time.ZonedDateTime;

import javax.validation.Valid;

import it.pagopa.bs.common.model.api.request.criteria.FieldSearchCriteria;
import it.pagopa.bs.common.model.api.request.criteria.RangeSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchSouthConfigRequest {

    @Valid
    private FieldSearchCriteria<String> southConfigId;

    @Valid
    private FieldSearchCriteria<String> description;

    @Valid
    private FieldSearchCriteria<String> southConfigCode;

    @Valid
    private FieldSearchCriteria<String> connectorName;

    @Valid
    private RangeSearchCriteria<Long> modelVersion;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> createdDatetime;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> updatedDatetime;
}

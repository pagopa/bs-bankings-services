package it.pagopa.bs.checkiban.model.api.request.config.entity;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import it.pagopa.bs.common.model.api.request.criteria.FieldSearchCriteria;
import it.pagopa.bs.common.model.api.request.criteria.RangeSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEntityRequest {

    @Valid
    private FieldSearchCriteria<String> name;

    @Valid
    private FieldSearchCriteria<String> supportEmail;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> createdDatetime;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> updatedDatetime;
}

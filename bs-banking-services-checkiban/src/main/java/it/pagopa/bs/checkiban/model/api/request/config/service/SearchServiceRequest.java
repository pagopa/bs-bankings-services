package it.pagopa.bs.checkiban.model.api.request.config.service;

import java.time.ZonedDateTime;

import javax.validation.Valid;

import it.pagopa.bs.common.enumeration.ServiceCode;
import it.pagopa.bs.common.model.api.request.criteria.FieldSearchCriteria;
import it.pagopa.bs.common.model.api.request.criteria.RangeSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchServiceRequest {

    @Valid
    private FieldSearchCriteria<ServiceCode> serviceCode;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> createdDatetime;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> updatedDatetime;
}

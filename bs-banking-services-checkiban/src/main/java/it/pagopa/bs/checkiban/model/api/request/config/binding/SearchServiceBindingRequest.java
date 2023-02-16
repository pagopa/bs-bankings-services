package it.pagopa.bs.checkiban.model.api.request.config.binding;

import java.time.ZonedDateTime;

import javax.validation.Valid;

import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.SearchPspRequest;
import it.pagopa.bs.checkiban.model.api.request.config.service.SearchServiceRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
import it.pagopa.bs.common.model.api.request.criteria.BooleanSearchCriteria;
import it.pagopa.bs.common.model.api.request.criteria.RangeSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchServiceBindingRequest {

    @Valid
    private BooleanSearchCriteria includeHistory;

    @Valid
    private SearchPspRequest psp;

    @Valid
    private SearchServiceRequest service;

    @Valid
    private SearchSouthConfigRequest southConfig;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> validityStartedDatetime;
}

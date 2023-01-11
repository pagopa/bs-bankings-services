package it.pagopa.bs.checkiban.model.api.request.config.binding;

import javax.validation.Valid;

import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.SearchPspRequest;
import it.pagopa.bs.checkiban.model.api.request.config.service.SearchServiceRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.shared.DateTimeRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchServiceBindingRequest {

    private boolean includeHistory;

    @Valid
    private SearchPspRequest psp;

    @Valid
    private SearchServiceRequest service;

    @Valid
    private SearchSouthConfigRequest southConfig;

    @Valid
    private DateTimeRange validityStartedDatetimeRange = new DateTimeRange();
}

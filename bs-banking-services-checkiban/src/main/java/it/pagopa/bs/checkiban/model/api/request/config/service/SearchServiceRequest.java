package it.pagopa.bs.checkiban.model.api.request.config.service;

import javax.validation.Valid;

import it.pagopa.bs.checkiban.model.api.shared.DateTimeRange;
import it.pagopa.bs.common.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchServiceRequest {

    private ServiceCode serviceCode;

    @Valid
    private DateTimeRange createdDatetimeRange = new DateTimeRange();
}

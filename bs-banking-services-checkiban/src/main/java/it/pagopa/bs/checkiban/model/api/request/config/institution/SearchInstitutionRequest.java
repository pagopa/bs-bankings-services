package it.pagopa.bs.checkiban.model.api.request.config.institution;

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
public class SearchInstitutionRequest {

    @Valid
    private FieldSearchCriteria<String> institutionId;

    @Valid
    private FieldSearchCriteria<String> name;

    @Valid
    private FieldSearchCriteria<String> institutionCode;

    @Valid
    private FieldSearchCriteria<String> cdcCode;

    @Valid
    private FieldSearchCriteria<String> cdcDescription;

    @Valid
    private FieldSearchCriteria<String> credentialId;

    @Valid
    private FieldSearchCriteria<String> fiscalCode;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> createdDatetime;

    @Valid
    private RangeSearchCriteria<ZonedDateTime> updatedDatetime;
}

package it.pagopa.bs.checkiban.model.api.request.config.institution;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import it.pagopa.bs.checkiban.model.api.shared.DateTimeRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchInstitutionRequest {

    @Size(max = 255)
    private String name;

    @Size(max = 100)
    private String institutionCode;

    @Size(max = 50)
    private String cdcCode;

    @Size(max = 50)
    private String credentialId;

    @Size(max = 100)
    private String fiscalCode;

    @Valid
    private DateTimeRange createdDatetimeRange = new DateTimeRange();
}

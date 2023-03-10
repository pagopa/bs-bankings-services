package it.pagopa.bs.checkiban.model.api.request.config.institution;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstitutionRequest {

    @NotEmpty
    @Size(max = 255)
    private String name;

    @Size(max = 100)
    private String institutionCode;

    @Size(max = 50)
    private String cdcCode = "CDC-001";

    @Size(max = 255)
    private String cdcDescription;

    @NotEmpty
    @Size(max = 50)
    private String credentialId;

    @Size(max = 100)
    private String fiscalCode;
}

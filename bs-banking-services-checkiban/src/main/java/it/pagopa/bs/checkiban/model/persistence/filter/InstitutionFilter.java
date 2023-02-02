package it.pagopa.bs.checkiban.model.persistence.filter;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionFilter {

    private String name;
    private String institutionCode;
    private String cdcCode;
    private String credentialId;
    private String fiscalCode;
    private ZonedDateTime createdStartDatetime;
    private ZonedDateTime createdEndDatetime;
}

package it.pagopa.bs.checkiban.model.persistence;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Institution implements Serializable {

    private long institutionId;
    private String institutionCode;
    private String name;
    private String cdcCode;
    private String cdcDescription;
    private String credentialId;
    private String fiscalCode;
    private ZonedDateTime createdDatetime;
    private ZonedDateTime updatedDatetime;
}

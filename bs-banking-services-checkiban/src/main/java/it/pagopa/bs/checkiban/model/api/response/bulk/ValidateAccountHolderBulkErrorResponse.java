package it.pagopa.bs.checkiban.model.api.response.bulk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateAccountHolderBulkErrorResponse {

    private int status;
    private String code;
    private String description;
    private String params;
}

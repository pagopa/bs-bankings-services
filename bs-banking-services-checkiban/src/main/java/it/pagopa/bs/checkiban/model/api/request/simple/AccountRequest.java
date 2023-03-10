package it.pagopa.bs.checkiban.model.api.request.simple;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import it.pagopa.bs.checkiban.util.validator.ValidAccountValueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest implements Serializable {

    @NotBlank
    private String value;

    @NotBlank
    @ValidAccountValueType
    private String valueType;

    private String bicCode;
}

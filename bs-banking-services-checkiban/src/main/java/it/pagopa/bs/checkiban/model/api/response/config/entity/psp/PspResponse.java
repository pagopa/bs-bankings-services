package it.pagopa.bs.checkiban.model.api.response.config.entity.psp;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.bs.checkiban.model.api.response.config.entity.EntityResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PspResponse extends EntityResponse {

    private String nationalCode;
    private String countryCode;
    private String bicCode;
    private String accountValueType;

    @JsonProperty("isBlacklisted")
    private boolean blacklisted;
}

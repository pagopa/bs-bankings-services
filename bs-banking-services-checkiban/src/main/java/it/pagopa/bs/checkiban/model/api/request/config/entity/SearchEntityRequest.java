package it.pagopa.bs.checkiban.model.api.request.config.entity;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import it.pagopa.bs.checkiban.model.api.shared.DateTimeRange;
import it.pagopa.bs.common.util.RegexPatterns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEntityRequest {

    @Size(max = 150)
    private String name;

    @Pattern(regexp = RegexPatterns.EMAIL_PATTERN)
    @Size(max = 255)
    private String supportEmail;

    @Valid
    private DateTimeRange createdDatetimeRange = new DateTimeRange();
}

package it.pagopa.bs.checkiban.model.api.request.config.south;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import it.pagopa.bs.checkiban.model.api.shared.DateTimeRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchSouthConfigRequest {

    @Size(max = 50)
    private String southConfigCode;

    @Size(max = 50)
    private String connectorName;

    private long modelVersion;

    @Valid
    private DateTimeRange createdDatetimeRange = new DateTimeRange();
}

package it.pagopa.bs.checkiban.model.api.request.config.south;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSouthConfigRequest {

    @Size(max = 255)
    private String description;

    @Size(max = 50)
    private String connectorName;
}

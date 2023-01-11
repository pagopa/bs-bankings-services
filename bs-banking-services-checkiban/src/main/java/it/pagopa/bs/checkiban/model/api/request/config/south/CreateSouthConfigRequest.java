package it.pagopa.bs.checkiban.model.api.request.config.south;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSouthConfigRequest {

    @NotEmpty
    @Size(max = 50)
    private String southConfigCode;

    @Size(max = 255)
    private String description;

    @NotEmpty
    @Size(max = 50)
    private String connectorName;
}

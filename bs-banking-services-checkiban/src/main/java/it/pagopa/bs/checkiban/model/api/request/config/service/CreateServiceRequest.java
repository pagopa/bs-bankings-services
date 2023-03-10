package it.pagopa.bs.checkiban.model.api.request.config.service;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import it.pagopa.bs.common.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceRequest {

    @NotNull
    private ServiceCode serviceCode;

    @Size(max = 255)
    private String description;
}

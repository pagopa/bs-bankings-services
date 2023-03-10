package it.pagopa.bs.checkiban.model.api.request.config.service;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceRequest {

    @Size(max = 255)
    private String description;
}

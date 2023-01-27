package it.pagopa.bs.checkiban.model.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

import it.pagopa.bs.common.enumeration.ServiceCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoPaService implements Serializable {

    private long serviceId;
    private ServiceCode serviceCode;
    private String description;
    private ZonedDateTime createdDatetime;
    private ZonedDateTime updatedDatetime;
}

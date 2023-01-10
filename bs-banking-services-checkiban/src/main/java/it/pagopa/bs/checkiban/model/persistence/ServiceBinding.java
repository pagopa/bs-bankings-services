package it.pagopa.bs.checkiban.model.persistence;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBinding implements Serializable {

    private long serviceBindingId;
    private ZonedDateTime validityStartedDatetime;
    private ZonedDateTime validityEndedDatetime;

    private Psp psp;
    private SouthConfig southConfig;
    private PagoPaService service;
}

package it.pagopa.bs.checkiban.model.persistence.filter;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBindingFilter {

    private boolean includeHistory;

    private PspFilter psp;
    private ServiceFilter service;
    private SouthConfigFilter southConfig;

    private ZonedDateTime validityStartedFromDatetime;
    private ZonedDateTime validityStartedToDatetime;
}

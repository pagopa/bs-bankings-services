package it.pagopa.bs.checkiban.model.persistence.filter;

import java.time.ZonedDateTime;

import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFilter {

    private ServiceCode serviceCode;
    private ZonedDateTime createdStartDatetime;
    private ZonedDateTime createdEndDatetime;
}

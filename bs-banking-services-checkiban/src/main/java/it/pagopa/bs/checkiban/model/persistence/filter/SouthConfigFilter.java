package it.pagopa.bs.checkiban.model.persistence.filter;

import java.time.ZonedDateTime;

import it.pagopa.bs.common.enumeration.ConnectorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SouthConfigFilter {

    private String southConfigCode;
    private String connectorName;
    private ConnectorType connectorType;
    private long modelVersion;
    private ZonedDateTime createdStartDatetime;
    private ZonedDateTime createdEndDatetime;
}
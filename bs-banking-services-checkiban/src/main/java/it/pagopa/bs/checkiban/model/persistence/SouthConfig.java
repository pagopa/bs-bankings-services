package it.pagopa.bs.checkiban.model.persistence;

import java.io.Serializable;
import java.time.ZonedDateTime;

import it.pagopa.bs.common.enumeration.ConnectorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SouthConfig implements Serializable {

    private long southConfigId;
    private String southConfigCode;
    private String description;
    private String connectorName;
    private ConnectorType connectorType;
    private long modelVersion;
    private String modelConfig;
    private ZonedDateTime createdDatetime;
    private ZonedDateTime updatedDatetime;

    private int resultCount; // for technical reasons
}

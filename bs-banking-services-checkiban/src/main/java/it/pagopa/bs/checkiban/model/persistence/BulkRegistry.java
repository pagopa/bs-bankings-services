package it.pagopa.bs.checkiban.model.persistence;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import it.pagopa.bs.checkiban.enumeration.BulkStatus;
import it.pagopa.bs.common.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkRegistry implements Serializable {

    private String bulkRequestId;
    private String correlationId;
    private String credentialId;
    private ServiceCode serviceCode;
    private int processedElementsCount;
    private int elementsCount;
    private ZonedDateTime insertedDatetime;
    private ZonedDateTime completedDatetime;
    private BulkStatus bulkStatus;
    private Boolean hasBatchElements;
    private Long routingTimeMs;

    private List<BulkElement> bulkElements;
    private List<BatchRegistry> batchRegistries;
}

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
public class BatchRegistry implements Serializable {

    private String bulkRequestId;
    private String connectorCode;
    private ZonedDateTime batchCreatedDatetime;
    private String batchFilename;
    private int batchElementsCount;
}

package it.pagopa.bs.checkiban.model.persistence;

import java.io.Serializable;
import java.time.ZonedDateTime;

import it.pagopa.bs.checkiban.enumeration.BulkElementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkElement implements Serializable {

    private String bulkRequestId;
    private String requestId;
    private String batchElementId;
    // private String requestJson; // TODO: remove since it's a waste of space
    private String responseJson;
    private BulkElementStatus elementStatus;
    private String batchElementConnector;
    private ZonedDateTime insertedDatetime;
    private ZonedDateTime lastUpdatedDatetime;

    private long pspId;
}

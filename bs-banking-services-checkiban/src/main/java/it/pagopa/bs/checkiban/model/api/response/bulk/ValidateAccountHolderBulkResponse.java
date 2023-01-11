package it.pagopa.bs.checkiban.model.api.response.bulk;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pagopa.bs.checkiban.enumeration.BulkStatus;
import it.pagopa.bs.common.deserializer.DateTimeDeserializer;
import it.pagopa.bs.common.serializer.DateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAccountHolderBulkResponse {

    private String bulkRequestId;
    private BulkStatus bulkRequestStatus;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime completedDatetime;

    private int processedItemsCount;
    private int totalItemsCount;

    private List<ValidateAccountHolderBulkElementResponse> list;
}

package it.pagopa.bs.checkiban.model.event;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pagopa.bs.common.deserializer.LocalDateDeserializer;
import it.pagopa.bs.common.enumeration.ServiceCode;
import it.pagopa.bs.common.serializer.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEventModel implements Serializable {

    private String eventUid;
    private String bulkRequestId;
    private String requestCode;
    private String correlationId;
    private String credentialId;
    private Long modelVersion;
    private ServiceCode serviceCode;
    private String destination;
    private String obo;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate referenceDate;

    private JsonNode payload;
}

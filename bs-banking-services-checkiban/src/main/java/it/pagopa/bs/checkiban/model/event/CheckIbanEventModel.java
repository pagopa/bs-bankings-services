package it.pagopa.bs.checkiban.model.event;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pagopa.bs.common.deserializer.DateTimeDeserializer;
import it.pagopa.bs.common.deserializer.LocalDateDeserializer;
import it.pagopa.bs.common.serializer.DateTimeSerializer;
import it.pagopa.bs.common.serializer.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CheckIbanEventModel implements Serializable {

    private String eventUid;
    private String bulkRequestId;
    private String requestCode;
    private Integer modelVersion;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate referenceDate;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime requestDatetime;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime responseDatetime;

    private String validationStatus;
    private Long responseTimeMs;

    private NorthInfoModel northInfo = new NorthInfoModel();
    private SouthInfoModel southInfo = new SouthInfoModel();

    private InstitutionInfoModel institutionInfo = new InstitutionInfoModel();
    private PspInfoModel pspInfo = new PspInfoModel();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NorthInfoModel implements Serializable {

        private Long routingTimeMs;
        private Integer responseStatus;
        private String responseCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SouthInfoModel implements Serializable {

        private Long processingTimeMs;
        private Integer responseStatus;
        private String connectorType;
        private String connectorName;
        private String batchFilename;

        @JsonSerialize(using = DateTimeSerializer.class)
        @JsonDeserialize(using = DateTimeDeserializer.class)
        private ZonedDateTime batchCreatedDatetime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstitutionInfoModel implements Serializable {

        private String institutionId;
        private String name;
        private String institutionCode;
        private String cdcCode;
        private String credentialId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PspInfoModel implements Serializable {

        private String pspId;
        private String name;
        private String bicCode;
        private String nationalCode;
        private String countryCode;
    }
}

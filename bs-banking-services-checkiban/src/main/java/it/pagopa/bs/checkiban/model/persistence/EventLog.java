package it.pagopa.bs.checkiban.model.persistence;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventLog implements Serializable {

    private String uid;
    private String credentialId;
    private String subscriptionId;

    private LocalDate referenceLocalDate;

    private String serviceCode;
    private String destinationTopic;
    private String xOboHeader;
    private String xOboPrimary;

    private String xCorrelationId;

    private long modelVersion;
    private String eventModel;
}

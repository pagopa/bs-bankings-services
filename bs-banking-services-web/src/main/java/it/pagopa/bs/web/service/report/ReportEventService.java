package it.pagopa.bs.web.service.report;

import reactor.core.publisher.Mono;

public interface ReportEventService {
    
    public Mono<String> produceEvent(
        String requestId,
        String destinationTopic,
        String payload
    );
}

package it.pagopa.bs.web.service.checkiban.bulk;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.pagopa.bs.checkiban.enumeration.BulkStatus;
import it.pagopa.bs.checkiban.model.api.request.bulk.ValidateAccountHolderBulkRequest;
import it.pagopa.bs.checkiban.model.api.response.bulk.ValidateAccountHolderBulkResponse;
import it.pagopa.bs.checkiban.model.persistence.BulkRegistry;
import it.pagopa.bs.checkiban.util.BulkConversionUtil;
import it.pagopa.bs.common.enumeration.ServiceCode;
import it.pagopa.bs.common.exception.BadRequestException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.util.DateUtil;
import it.pagopa.bs.web.mapper.bulk.BulkRegistryMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CheckIbanBulkService {

    private final BulkRegistryMapper bulkRegistryMapper;

    private final CheckIbanBulkOperationsService bulkOperations;

    @Value("${pagopa.bs.bulk-size}")
    private int bulkSize; 
    
    @Transactional
    public Mono<ValidateAccountHolderBulkResponse> checkIbanSimpleBulk(
        List<ValidateAccountHolderBulkRequest> list,
        String correlationId,
        String credentialId
    ) {
        long startingTimeMs = System.currentTimeMillis();

        if(list.isEmpty()) {
            throw new BadRequestException("Too few items, the minimum is 1");
        } else if(list.size() > bulkSize) {
            throw new BadRequestException("Too many items, the cap is " + bulkSize);
        }

        final String bulkRequestId = UUID.randomUUID().toString();

        bulkRegistryMapper.insertBulkRegistry(
            bulkRequestId,
            correlationId,
            credentialId,
            ServiceCode.CHECK_IBAN_SIMPLE,
            BulkStatus.PENDING,
            list.size()
        );

        bulkOperations.insertBulkElements(
            list.stream()
                .map(le -> BulkConversionUtil.map(bulkRequestId, UUID.randomUUID().toString(), le))
                .collect(Collectors.toList())
        );

        BulkRegistry createdBulkRegistry = bulkRegistryMapper.getWithElementsByBulkRequestId(bulkRequestId);
        if(createdBulkRegistry == null) {
            throw new ResourceNotFoundException("Bulk Request");
        }

        bulkRegistryMapper.setRoutingTimeMs(
                bulkRequestId,
                System.currentTimeMillis() - startingTimeMs
        ); // we need this later to compute the events for the single check iban requests

        return Mono.just(
            ValidateAccountHolderBulkResponse.builder()
                .bulkRequestId(bulkRequestId)
                .bulkRequestStatus(BulkStatus.PENDING)
                .completedDatetime(null)
                .processedItemsCount(0)
                .totalItemsCount(list.size())
                .list(BulkConversionUtil.map(createdBulkRegistry.getBulkElements()))
                .build()
        );
    }

    public Mono<ValidateAccountHolderBulkResponse> getCheckIbanSimpleBulkResponse(
        String bulkRequestId,
        String correlationId,
        String credentialId
    ) {
        BulkRegistry existingBulkRegistry = bulkRegistryMapper.getWithElementsByBulkRequestIdAndCredentialId(
                bulkRequestId,
                credentialId
        );
        if(existingBulkRegistry == null) {
            throw new ResourceNotFoundException("Bulk Request");
        }

        return Mono.just(
            ValidateAccountHolderBulkResponse.builder()
                .bulkRequestId(bulkRequestId)
                .bulkRequestStatus(existingBulkRegistry.getBulkStatus())
                .completedDatetime(DateUtil.fromLocalToUtc(existingBulkRegistry.getCompletedDatetime()))
                .processedItemsCount(existingBulkRegistry.getProcessedElementsCount())
                .totalItemsCount(existingBulkRegistry.getElementsCount())
                .list(BulkConversionUtil.map(existingBulkRegistry.getBulkElements()))
                .build()
        );
    }
}

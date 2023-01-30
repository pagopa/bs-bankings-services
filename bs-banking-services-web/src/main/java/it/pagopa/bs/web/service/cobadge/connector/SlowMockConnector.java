package it.pagopa.bs.web.service.cobadge.connector;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import it.pagopa.bs.cobadge.enumeration.ExecutionStatus;
import it.pagopa.bs.cobadge.enumeration.PaymentNetworks;
import it.pagopa.bs.cobadge.enumeration.ProductType;
import it.pagopa.bs.cobadge.enumeration.ValidityStatus;
import it.pagopa.bs.cobadge.model.api.request.PaymentInstrumentRequest;
import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentSouthResponse;
import it.pagopa.bs.cobadge.util.PaymentUtil;
import it.pagopa.bs.common.client.RestClient;
import it.pagopa.bs.web.mapper.PaymentInstrumentsMapper;
import it.pagopa.bs.web.service.conf.WhitelistService;
import it.pagopa.bs.web.service.crypto.PGPCryptoService;
import reactor.core.publisher.Mono;

@Service
public class SlowMockConnector extends AConnector {

    private Map<String, List<PaymentInstrumentSouthResponse>> mockModelsMap;

    SlowMockConnector(
            PaymentInstrumentsMapper paymentInstrumentsOperations,
            PGPCryptoService cryptoService,
            WhitelistService whitelistService
    ) {
        super(paymentInstrumentsOperations, cryptoService);
        this.mockModelsMap = this.buildMockModels(whitelistService.getFiscalCodeWhitelist());
    }

    @Override
    Mono<List<PaymentInstrumentSouthResponse>> callService(
            RestClient client,
            PaymentInstrumentRequest inputModel,
            String searchRequestId,
            String southPath
    ) {
        List<PaymentInstrumentSouthResponse> results = mockModelsMap.get(inputModel.getFiscalCode());
        if(results.get(0).isEmpty()) {
            return Mono.just(results);
        }

        // Simulate RestClient behaviour
        long timeout = 60;
        if(paymentInstrumentsOperations
                .existsRequestFromServiceProvider(searchRequestId, this.serviceProviderName()) == 1) {
            timeout = 2; // done to speed up testing
        }

        if(!StringUtils.isEmpty(inputModel.getAbiCode())) {
            results = results.stream()
                    .filter(r -> r.getAbiCode().equals(inputModel.getAbiCode())).collect(Collectors.toList());

            if(results.isEmpty()) {
                results = PaymentUtil.buildEmptyModel(this.serviceProviderName());
            }
        }

        return Mono.just(results)
            .delayElement(Duration.ofSeconds(10))
            .timeout(Duration.ofSeconds(timeout));
    }

    private Map<String, List<PaymentInstrumentSouthResponse>> buildMockModels(
            List<String> fiscalCodes
    ) {
        Map<String, List<PaymentInstrumentSouthResponse>> result = new HashMap<>();
        List<PaymentInstrumentSouthResponse> mockModelList;

        // 1
        mockModelList = new ArrayList<>();
        mockModelList.add(
            PaymentInstrumentSouthResponse
                .builder()
                .panCode("pan-di-spagna")
                .abiCode("12345")
                .productType(ProductType.DEBIT.name())
                .validityStatus(ValidityStatus.BLOCK_REVERSIBLE.name())
                .expiringDate(LocalDate.of(2067, 10, 15).toString())
                .paymentNetwork(PaymentNetworks.MAESTRO.name())
                .executionStatus(ExecutionStatus.OK.name())
                .empty(false)
                .failed(false)
                .serviceProviderName(this.serviceProviderName())
                .build()
        );

        result.put(fiscalCodes.get(0), mockModelList);

        // 2
        result.put(fiscalCodes.get(1), PaymentUtil.buildEmptyModel(this.serviceProviderName()));

        // 3
        result.put(fiscalCodes.get(2), PaymentUtil.buildEmptyModel(this.serviceProviderName()));

        // 4
        mockModelList = new ArrayList<>();
        mockModelList.add(
            PaymentInstrumentSouthResponse
                .builder()
                .panCode("pan-e-angelo")
                .abiCode("12345")
                .productType(ProductType.DEBIT.name())
                .validityStatus(ValidityStatus.VALID.name())
                .expiringDate(LocalDate.of(2036, 10, 15).toString())
                .paymentNetwork(PaymentNetworks.VISA_CLASSIC.name())
                .executionStatus(ExecutionStatus.OK.name())
                .empty(false)
                .failed(false)
                .serviceProviderName(this.serviceProviderName())
                .build()
        );
        mockModelList.add(
            PaymentInstrumentSouthResponse
                .builder()
                .panCode("pan-a-cea")
                .abiCode("54321")
                .productType(ProductType.PREPAID.name())
                .validityStatus(ValidityStatus.VALID.name())
                .expiringDate(LocalDate.of(2048, 10, 16).toString())
                .paymentNetwork(PaymentNetworks.VISA_ELECTRON.name())
                .executionStatus(ExecutionStatus.OK.name())
                .empty(false)
                .failed(false)
                .serviceProviderName(this.serviceProviderName())
                .build()
        );

        result.put(fiscalCodes.get(3), mockModelList);

        return result;
    }

    @Override
    public String serviceProviderName() {
        return "SLOW_MOCK";
    }
}

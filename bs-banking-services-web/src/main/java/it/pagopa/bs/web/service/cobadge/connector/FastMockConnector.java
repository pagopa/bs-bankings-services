package it.pagopa.bs.web.service.cobadge.connector;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
public class FastMockConnector extends AConnector {

    private Map<String, List<PaymentInstrumentSouthResponse>> mockModelsMap;

    FastMockConnector(
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
        // Filter results
        List<PaymentInstrumentSouthResponse> results = mockModelsMap.get(inputModel.getFiscalCode());
        if(!StringUtils.isEmpty(inputModel.getAbiCode())) {
            results = results.stream()
                    .filter(r -> r.getAbiCode().equals(inputModel.getAbiCode())).collect(Collectors.toList());

            if(results.isEmpty()) {
                results = PaymentUtil.buildEmptyModel(this.serviceProviderName());
            }
        }

        return Mono.just(results);
    }

    private Map<String, List<PaymentInstrumentSouthResponse>> buildMockModels(
            List<String> fiscalCodes
    ) {
        Map<String, List<PaymentInstrumentSouthResponse>> result = new HashMap<>();
        List<PaymentInstrumentSouthResponse> fastModelList;

        // 1
        fastModelList = new ArrayList<>();
        fastModelList.add(
            PaymentInstrumentSouthResponse
                .builder()
                .panCode("pan-di-zenzero")
                .abiCode("12345")
                .productType(ProductType.PREPAID.name())
                .validityStatus(ValidityStatus.VALID.name())
                .expiringDate(LocalDate.of(2067, 10, 15).toString())
                .paymentNetwork(PaymentNetworks.MASTERCARD.name())
                .executionStatus(ExecutionStatus.OK.name())
                .empty(false)
                .failed(false)
                .serviceProviderName(this.serviceProviderName())
                .build()
        );

        result.put(fiscalCodes.get(0), fastModelList);

        // 2
        result.put(fiscalCodes.get(1), PaymentUtil.buildEmptyModel(this.serviceProviderName()));

        // 3
        result.put(fiscalCodes.get(2), Collections.singletonList(PaymentInstrumentSouthResponse
            .builder()
            .panCode("pan-dei-toast")
            .abiCode("54321")
            .productType(ProductType.PREPAID.name())
            .validityStatus(ValidityStatus.VALID.name())
            .expiringDate(LocalDate.of(2011, 10, 15).toString())
            .paymentNetwork(PaymentNetworks.MASTERCARD.name())
            .executionStatus(ExecutionStatus.OK.name())
            .empty(false)
            .failed(false)
            .serviceProviderName(this.serviceProviderName())
            .build()
        ));

        // 4
        fastModelList = new ArrayList<>();
        fastModelList.add(
            PaymentInstrumentSouthResponse
                .builder()
                .panCode("pan-di-stelle")
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
        fastModelList.add(
            PaymentInstrumentSouthResponse
                .builder()
                .panCode("pan-bauletto")
                .abiCode("54321")
                .productType(ProductType.CREDIT.name())
                .validityStatus(ValidityStatus.VALID.name())
                .expiringDate(LocalDate.of(2037, 10, 15).toString())
                .paymentNetwork(PaymentNetworks.MASTERCARD.name())
                .executionStatus(ExecutionStatus.OK.name())
                .empty(false)
                .failed(false)
                .serviceProviderName(this.serviceProviderName())
                .build()
        );

        result.put(fiscalCodes.get(3), fastModelList);

        return result;
    }

    @Override
    public String serviceProviderName() {
        return "FAST_MOCK";
    }
}

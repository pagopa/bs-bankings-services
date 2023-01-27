package it.pagopa.bs.web.service.cobadge;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import it.pagopa.bs.cobadge.model.api.request.PaymentInstrumentRequest;
import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentResponse;
import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentSouthResponse;
import it.pagopa.bs.cobadge.model.persistence.PaymentInstrumentsOp;
import it.pagopa.bs.common.client.RestClient;
import it.pagopa.bs.common.enumeration.ErrorCodes;
import it.pagopa.bs.common.exception.BadRequestException;
import it.pagopa.bs.common.exception.ParsingException;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.PaymentInstrumentsMapper;
import it.pagopa.bs.web.service.cobadge.connector.AConnector;
import it.pagopa.bs.web.service.conf.WhitelistService;
import it.pagopa.bs.web.service.domain.ServiceProviderWithConfig;
import it.pagopa.bs.web.service.registry.ServiceProviderRegistry;
import reactor.core.publisher.Mono;

@Service
public class PaymentInstrumentsService {
    
    private final RestClient restClient;
    private final Aggregator aggregator;
    private final PaymentInstrumentsMapper paymentInstrumentsOperations;

    private final WhitelistService whitelistService;
    private final ServiceProviderRegistry serviceProviderRegistry;

    public PaymentInstrumentsService(
            RestClient restClient,
            PaymentInstrumentsMapper paymentInstrumentsOperations,
            Aggregator aggregator,
            WhitelistService whitelistService,
            ServiceProviderRegistry serviceProviderRegistry
    ) {
        this.restClient = restClient;
        this.aggregator = aggregator;
        this.paymentInstrumentsOperations = paymentInstrumentsOperations;
        this.whitelistService = whitelistService;
        this.serviceProviderRegistry = serviceProviderRegistry;
    }

    public Mono<PaymentInstrumentResponse> search(PaymentInstrumentRequest inputModel) {
        String searchRequestId = UUID.randomUUID().toString();
        String request = parseRequest(inputModel);

        List<Mono<List<PaymentInstrumentSouthResponse>>> requests;

        if(whitelistService.isInWhitelist(inputModel.getFiscalCode())) {
            requests = buildMockRequests(inputModel, request, searchRequestId);
        } else if(!StringUtils.isEmpty(inputModel.getAbiCode())) {
            requests = buildSpecificRequest(inputModel, request, searchRequestId);
        } else {
            requests = buildStandardRequests(inputModel, request, searchRequestId);
        }

        if(requests.isEmpty()) {
            return Mono.just(
                    new PaymentInstrumentResponse(searchRequestId, new LinkedList<>(), new LinkedList<>())
            );
        }

        return aggregator.aggregate(searchRequestId, requests);
    }

    private String parseRequest(PaymentInstrumentRequest inputModel) {
        try {
            return JsonUtil.toString(inputModel);
        } catch (JsonProcessingException e) {
            throw new ParsingException(e.getMessage());
        }
    }

    private List<Mono<List<PaymentInstrumentSouthResponse>>> buildMockRequests(
            PaymentInstrumentRequest inputModel,
            String rawRequest,
            String searchRequestId
    ) {
        Collection<ServiceProviderWithConfig> serviceProviders = serviceProviderRegistry.getServiceProviders();
        List<Mono<List<PaymentInstrumentSouthResponse>>> requests = new LinkedList<>();

        serviceProviders.forEach(sp -> {
            if(isValidMock(sp)) {
                paymentInstrumentsOperations.insert(searchRequestId, sp.getName(), rawRequest);
                requests.add(sp.getConnector().remoteCall(restClient, searchRequestId, inputModel, sp.getSouthPath()));
            }
        });

        return requests;
    }

    private boolean isValidMock(ServiceProviderWithConfig serviceProvider) {
        return serviceProvider.isMock() && serviceProvider.isActive();
    }

    private List<Mono<List<PaymentInstrumentSouthResponse>>> buildSpecificRequest(
            PaymentInstrumentRequest inputModel,
            String rawRequest,
            String searchRequestId
    ) {
        ServiceProviderWithConfig serviceProvider =
                serviceProviderRegistry.getServiceProviderFromAbi(inputModel.getAbiCode());

        if (isNullOrInvalid(serviceProvider)) {
            throw new BadRequestException(ErrorCodes.INVALID_BODY_PARAMETERS, "Abi not handled by any service provider");
        }

        if (isPrivativeAndPanEmpty(serviceProvider, inputModel.getPanCode())) {
            throw new BadRequestException(ErrorCodes.INVALID_BODY_PARAMETERS, "PAN required for PRIVATIVE service providers");
        }

        if (isStandardAndHasPan(serviceProvider, inputModel.getPanCode())) {
            throw new BadRequestException(ErrorCodes.INVALID_BODY_PARAMETERS, "PAN can be included only for PRIVATIVE service providers");
        }

        AConnector connector = serviceProvider.getConnector();

        paymentInstrumentsOperations.insert(searchRequestId, serviceProvider.getName(), rawRequest);
        return Collections.singletonList(
                connector.remoteCall(restClient, searchRequestId, inputModel, serviceProvider.getSouthPath())
        );
    }

    private boolean isNullOrInvalid(ServiceProviderWithConfig serviceProvider) {
        return serviceProvider == null ||
                serviceProvider.getName() == null ||
                !serviceProvider.isActive() ||
                serviceProvider.isMock();
    }

    private boolean isPrivativeAndPanEmpty(ServiceProviderWithConfig sp, String panCode) {
        return sp.isPrivative() && StringUtils.isEmpty(panCode);
    }

    private boolean isStandardAndHasPan(ServiceProviderWithConfig sp, String panCode) {
        return !sp.isPrivative() && !StringUtils.isEmpty(panCode);
    }

    private List<Mono<List<PaymentInstrumentSouthResponse>>> buildStandardRequests(
            PaymentInstrumentRequest inputModel,
            String rawRequest,
            String searchRequestId
    ) {
        Collection<ServiceProviderWithConfig> serviceProviders = serviceProviderRegistry.getServiceProviders();
        List<Mono<List<PaymentInstrumentSouthResponse>>> requests = new LinkedList<>();

        serviceProviders.forEach(sp -> {
            if(isStandardAndValid(sp)) {
                paymentInstrumentsOperations.insert(searchRequestId, sp.getName(), rawRequest);
                requests.add(sp.getConnector().remoteCall(restClient, searchRequestId, inputModel, sp.getSouthPath()));
            }
        });

        return requests;
    }

    private boolean isStandardAndValid(ServiceProviderWithConfig serviceProvider) {
        return serviceProvider.isActive() && !serviceProvider.isMock() && serviceProvider.isHasGenericSearch();
    }

    public Mono<PaymentInstrumentResponse> getSearchResult(String searchRequestId) {
        List<Mono<List<PaymentInstrumentSouthResponse>>> completeServiceProviderResponse = new LinkedList<>();
        List<PaymentInstrumentsOp> operations = paymentInstrumentsOperations.getRequest(searchRequestId);

        if (operations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find searchRequestId");
        }

        for (PaymentInstrumentsOp operation : operations) {
            completeServiceProviderResponse.add(buildCompleteResponseFromOperation(operation));
        }

        return aggregator.aggregate(searchRequestId, completeServiceProviderResponse);
    }

    private Mono<List<PaymentInstrumentSouthResponse>> buildCompleteResponseFromOperation(PaymentInstrumentsOp operation) {
        Mono<List<PaymentInstrumentSouthResponse>> singleServiceProviderResponse = null;

        switch (operation.getExecutionStatus()) {
            case OK:
                singleServiceProviderResponse = buildOkResponse(operation);
                break;
            case PENDING:
            case KO:
                singleServiceProviderResponse = buildFailResponse(operation);
                break;
            default:
                break;
        }

        return singleServiceProviderResponse;
    }

    private Mono<List<PaymentInstrumentSouthResponse>> buildOkResponse(PaymentInstrumentsOp operation) {
        List<PaymentInstrumentSouthResponse> completedResponse = parseResponse(operation.getResponse());

        completedResponse.forEach(r -> {
            r.setServiceProviderName(operation.getServiceProviderName());
            r.setExecutionStatus(operation.getExecutionStatus().name());

            if(r.getPanCode() != null) {
                r.setEmpty(false);
            }
        });

        return Mono.just(completedResponse);
    }

    private List<PaymentInstrumentSouthResponse> parseResponse(String response) {
        try {
            return JsonUtil.fromString(
                    response,
                    new TypeReference<List<PaymentInstrumentSouthResponse>>() {}
            );
        } catch (IOException e) {
            throw new ParsingException(e.getMessage());
        }
    }

    private Mono<List<PaymentInstrumentSouthResponse>> buildFailResponse(PaymentInstrumentsOp operation) {
        PaymentInstrumentSouthResponse failureModel = new PaymentInstrumentSouthResponse();
        failureModel.setServiceProviderName(operation.getServiceProviderName());
        failureModel.setFailed(true);
        failureModel.setExecutionStatus(operation.getExecutionStatus().name());

        return Mono.just(Collections.singletonList(failureModel));
    }
}

package it.pagopa.bs.web.mapper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.cobadge.model.persistence.PaymentInstrumentsOp;

@Mapper
public interface PaymentInstrumentsOperations {

    void insert(@Param("uuid") String uuid, @Param("serviceProviderName") String serviceProviderName, @Param("request") String request);

    void success(@Param("uuid") String uuid, @Param("serviceProviderName") String serviceProviderName, @Param("response") String response);

    void fail(@Param("uuid") String uuid, @Param("serviceProviderName") String serviceProviderName);

    void failPermanent(@Param("uuid") String uuid, @Param("serviceProviderName") String serviceProviderName);

    List<PaymentInstrumentsOp> getFailed(
            @Param("timeout") int timeout,
            @Param("parallelism") int parallelism,
            @Param("currentDatetimeMinusDelay") LocalDateTime currentDatetimeMinusDelay
    );

    List<PaymentInstrumentsOp> getOld(
            @Param("parallelism") int parallelism,
            @Param("currentDatetimeMinusDelay") ZonedDateTime currentDatetimeMinusDelay
    );

    void deleteByUuid(@Param("uuid") String uuid);

    List<PaymentInstrumentsOp> getRequest(@Param("uuid") String uuid);

    int existsRequestFromServiceProvider(
            @Param("uuid") String uuid,
            @Param("serviceProviderName") String serviceProviderName
    );

    void overrideRequestAndResponse(
            @Param("recordsToUpdate") List<PaymentInstrumentsOp> recordsToUpdate,
            @Param("replacementRequest") String replacementRequest,
            @Param("replacementResponse") String replacementResponse
    );

    void deleteOldRecords(@Param("recordsToDelete") List<PaymentInstrumentsOp> recordsToDelete);
}


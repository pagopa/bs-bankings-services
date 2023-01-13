package it.pagopa.bs.web.service;

import org.springframework.stereotype.Service;
import it.pagopa.bs.checkiban.exception.DetailedExceptionWrapper;
import it.pagopa.bs.checkiban.exception.InvalidIBANException;
import it.pagopa.bs.checkiban.model.iban.IBANElements;
import it.pagopa.bs.checkiban.model.iban.ValidateIbanResponse;
import it.pagopa.bs.common.enumeration.ErrorCodes;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import nl.garvelink.iban.IBAN;
import nl.garvelink.iban.IBANFields;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@CustomLog
public class IbanServices {

    public Mono<ValidateIbanResponse> validateIbanFormat(String correlationId, String iban) {

        IBAN ibanModel = null;
        try {
            ibanModel = IBAN.parse(iban);
        } catch(Exception e) {
            log.error(e);
            return Mono.error(new DetailedExceptionWrapper(new InvalidIBANException(), ErrorCodes.INVALID_IBAN));
        }

        final String nationalCode = IBANFields.getBankIdentifier(ibanModel).get();
        final String branchCode = IBANFields.getBranchIdentifier(ibanModel).get();
        final String countryCode = ibanModel.getCountryCode();

        return Mono.just(
            ValidateIbanResponse.builder()
                .iban(iban)
                .elements(
                    IBANElements.builder()
                        .nationalCode(nationalCode)
                        .countryCode(countryCode)
                        .branchCode(branchCode)
                        .build()
                )
                .bankInfo(null) // not used
                .build()
        );
    }
}

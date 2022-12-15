package it.pagopa.bs.common.util;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import it.pagopa.bs.common.enumeration.ResponseStatus;
import it.pagopa.bs.common.model.api.ResponseModel;

public class ResponseBuilder {

    private ResponseBuilder() {}

    public static <T> ResponseEntity<ResponseModel<T>> buildResponse(T payload, HttpStatus status) {
        return ResponseBuilder.buildResponse(payload, null, status);
    }

    public static <T> ResponseEntity<ResponseModel<T>> buildResponse(T payload, HttpHeaders headers, HttpStatus status) {
        ResponseModel<T> responseModel = new ResponseModel<>(ResponseStatus.OK, payload);
        responseModel.setErrors(Collections.emptyList());

        return new ResponseEntity<>(responseModel, headers, status);
    }
}

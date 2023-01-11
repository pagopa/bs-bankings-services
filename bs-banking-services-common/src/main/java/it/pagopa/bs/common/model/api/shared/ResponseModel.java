package it.pagopa.bs.common.model.api.shared;

import java.io.Serializable;
import java.util.Collection;

import it.pagopa.bs.common.enumeration.ResponseStatus;
import lombok.Data;

@Data
public class ResponseModel<T> implements Serializable {

    private ResponseStatus status;
    private T payload;
    private Collection<ErrorModel> errors;
    private Collection<ErrorModel> warnings;

    public ResponseModel() {}

    public ResponseModel(ResponseStatus status) {
        this.status = status;
    }

    public ResponseModel(ResponseStatus status, T payload) {
        this.status = status;
        this.payload = payload;
    }

    public ResponseModel(ResponseStatus status, Collection<ErrorModel> errors) {
        this.status = status;
        this.errors = errors;
    }
}

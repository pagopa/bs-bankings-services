package it.pagopa.bs.common.model.api.shared;

import java.io.Serializable;

import lombok.Data;

@Data
public class ErrorModel implements Serializable {
    
    private String code;
    private String description;
    private String params;

    public ErrorModel() {}

    public ErrorModel(Throwable throwable) {
        this.code = "ERR001";
        this.description = throwable.getMessage();
    }

    public ErrorModel(String code) {
        this.code = code;
    }

    public ErrorModel(String code, Throwable throwable) {
        this.code = code;
        this.description = throwable.getMessage();
    }

    public ErrorModel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public ErrorModel(String code, String description, String params) {
        this.code = code;
        this.description = description;
        this.params = params;
    }
}

package it.pagopa.bs.common.model.api.request;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ListRequestModel<T> implements Serializable {
    
    private @Valid List<T> list;
}

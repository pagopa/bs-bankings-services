package it.pagopa.bs.checkiban.model.persistence;

import java.io.Serializable;

import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Whitelist implements Serializable {

    private ServiceCode serviceCode;
    private String responseKey;
    private String responseValue;
}

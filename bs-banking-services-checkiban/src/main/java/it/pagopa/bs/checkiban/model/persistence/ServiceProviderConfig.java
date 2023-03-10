package it.pagopa.bs.checkiban.model.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProviderConfig {

    private String southPath;

    @JsonProperty("isPrivative")
    private boolean privative;

    @JsonProperty("isMock")
    private boolean mock;

    @JsonProperty("isActive")
    private boolean active;

    private boolean hasGenericSearch;
}

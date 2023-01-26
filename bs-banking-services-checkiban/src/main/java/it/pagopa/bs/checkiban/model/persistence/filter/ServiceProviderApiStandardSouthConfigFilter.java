package it.pagopa.bs.checkiban.model.persistence.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceProviderApiStandardSouthConfigFilter extends SouthConfigFilter {

    private String southPath;
    private Boolean hasGenericSearch;
    private Boolean isPrivative;
    private Boolean isActive;
}

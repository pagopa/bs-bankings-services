package it.pagopa.bs.checkiban.model.persistence;

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
public class ServiceProvider extends Entity {

    private long serviceProviderId;
    private boolean privative;
    private boolean mock;
}

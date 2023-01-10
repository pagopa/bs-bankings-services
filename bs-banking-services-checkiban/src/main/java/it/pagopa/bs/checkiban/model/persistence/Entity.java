package it.pagopa.bs.checkiban.model.persistence;

import java.io.Serializable;
import java.time.ZonedDateTime;

import it.pagopa.bs.checkiban.enumeration.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Entity implements Serializable {

    private long entityId;
    private String name;
    private String supportEmail;
    private EntityType type;
    private ZonedDateTime createdDatetime;
    private ZonedDateTime updatedDatetime;
}

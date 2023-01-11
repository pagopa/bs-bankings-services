package it.pagopa.bs.checkiban.model.persistence.filter;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EntityFilter {

    private String name;
    private String supportEmail;
    private ZonedDateTime createdStartDatetime;
    private ZonedDateTime createdEndDatetime;
}

package it.pagopa.bs.web.service.sorting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.pagopa.bs.common.sorting.SortableFields;

@Service
public class ServiceBindingSortableFields extends SortableFields {
    
    private final Map<String, String> allowedSortingProperties = new HashMap<>();

    public ServiceBindingSortableFields() {
        allowedSortingProperties.put("serviceBindingId", "sbi_id");
        allowedSortingProperties.put("validityStartedDatetime", "sbi_validity_started_datetime");
        allowedSortingProperties.put("validityEndedDatetime", "sbi_validity_ended_datetime");
    }

    @Override
    protected Map<String, String> getAllowedSortingProperties() {
        return allowedSortingProperties;
    }
}

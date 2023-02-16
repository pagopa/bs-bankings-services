package it.pagopa.bs.web.service.sorting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.pagopa.bs.common.sorting.SortableFields;

@Service
public class EntitySortableFields extends SortableFields {
    
    private final Map<String, String> allowedSortingProperties = new HashMap<>();

    public EntitySortableFields() {
        allowedSortingProperties.put("entityId", "en_id");
        allowedSortingProperties.put("name", "en_name");
        allowedSortingProperties.put("supportEmail", "en_support_email");
        allowedSortingProperties.put("createdDatetime", "en_created_datetime");
        allowedSortingProperties.put("updatedDatetime", "en_updated_datetime");
    }

    @Override
    protected Map<String, String> getAllowedSortingProperties() {
        return allowedSortingProperties;
    }
}

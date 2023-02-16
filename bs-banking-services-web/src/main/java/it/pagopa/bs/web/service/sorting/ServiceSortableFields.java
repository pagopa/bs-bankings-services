package it.pagopa.bs.web.service.sorting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.pagopa.bs.common.sorting.SortableFields;

@Service
public class ServiceSortableFields extends SortableFields {
    
    private final Map<String, String> allowedSortingProperties = new HashMap<>();

    public ServiceSortableFields() {
        allowedSortingProperties.put("serviceId", "srv_id");
        allowedSortingProperties.put("serviceCode", "srv_service_code");
        allowedSortingProperties.put("description", "srv_description");
        allowedSortingProperties.put("createdDatetime", "srv_created_datetime");
        allowedSortingProperties.put("updatedDatetime", "srv_updated_datetime");
    }

    @Override
    protected Map<String, String> getAllowedSortingProperties() {
        return allowedSortingProperties;
    }
}

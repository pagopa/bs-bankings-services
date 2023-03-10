package it.pagopa.bs.web.service.sorting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.pagopa.bs.common.sorting.SortableFields;

@Service
public class SouthConfigSortableFields extends SortableFields {
    
    private final Map<String, String> allowedSortingProperties = new HashMap<>();

    public SouthConfigSortableFields() {
        allowedSortingProperties.put("southConfigId", "scf_id");
        allowedSortingProperties.put("southConfigCode", "scf_code");
        allowedSortingProperties.put("description", "scf_description");
        allowedSortingProperties.put("connectorName", "scf_connector_name");
        allowedSortingProperties.put("connectorType", "scf_connector_type");
        allowedSortingProperties.put("modelVersion", "scf_model_version");
        allowedSortingProperties.put("createdDatetime", "scf_created_datetime");
        allowedSortingProperties.put("updatedDatetime", "scf_updated_datetime");
    }

    @Override
    protected Map<String, String> getAllowedSortingProperties() {
        return allowedSortingProperties;
    }
}

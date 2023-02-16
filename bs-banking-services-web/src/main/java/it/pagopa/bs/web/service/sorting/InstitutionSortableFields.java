package it.pagopa.bs.web.service.sorting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.pagopa.bs.common.sorting.SortableFields;

@Service
public class InstitutionSortableFields extends SortableFields {
    
    private final Map<String, String> allowedSortingProperties = new HashMap<>();

    public InstitutionSortableFields() {
        allowedSortingProperties.put("institutionId", "ins_id");
        allowedSortingProperties.put("institutionCode", "ins_code");
        allowedSortingProperties.put("name", "ins_name");
        allowedSortingProperties.put("cdcCode", "ins_cdc_code");
        allowedSortingProperties.put("cdcDescription", "ins_cdc_description");
        allowedSortingProperties.put("credentialId", "ins_credential_id");
        allowedSortingProperties.put("fiscalCode", "ins_fiscal_code");
        allowedSortingProperties.put("createdDatetime", "ins_created_datetime");
        allowedSortingProperties.put("updatedDatetime", "ins_updated_datetime");
    }

    @Override
    protected Map<String, String> getAllowedSortingProperties() {
        return allowedSortingProperties;
    }
}

package it.pagopa.bs.web.service.sorting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.pagopa.bs.common.sorting.SortableFields;

@Service
public class PspEntitySortableFields extends SortableFields {
    
    private final Map<String, String> allowedSortingProperties = new HashMap<>();

    public PspEntitySortableFields() {
        allowedSortingProperties.put("pspId", "psp_id");
        allowedSortingProperties.put("nationalCode", "psp_national_code");
        allowedSortingProperties.put("countryCode", "psp_country_code");
        allowedSortingProperties.put("bicCode", "psp_bic_code");
        allowedSortingProperties.put("accountValueType", "psp_account_value_type");
        allowedSortingProperties.put("blacklisted", "psp_is_blacklisted");
    }

    @Override
    protected Map<String, String> getAllowedSortingProperties() {
        return allowedSortingProperties;
    }
}

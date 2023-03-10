package it.pagopa.bs.common.sorting;

import java.util.Map;

public abstract class SortableFields {
    
    public String getInternalProperty(String externalProperty) {
        return this.getAllowedSortingProperties().getOrDefault(externalProperty, null);
    }

    protected abstract Map<String, String> getAllowedSortingProperties();
}

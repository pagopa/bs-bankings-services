package it.pagopa.bs.common.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.pagopa.bs.common.enumeration.SortingDirection;
import it.pagopa.bs.common.model.api.shared.SortingModel;
import it.pagopa.bs.common.sorting.SortableFields;

public class SortingUtil {

    private SortingUtil() { /* Intentionally empty */ }

    public static List<SortingModel> convertSortingFieldsToColumns(
        List<SortingModel> sortingModel, 
        String defaultFieldName, 
        SortableFields sortableFields
    ) {
        List<SortingModel> defaultSorting = Collections.singletonList(
            new SortingModel(SortingDirection.ASCENDING, sortableFields.getInternalProperty(defaultFieldName))
        );
        
        if (sortingModel != null && !sortingModel.isEmpty()) {
            List<SortingModel> sortingModelWithColumnNames = new LinkedList<>();
            sortingModel.forEach((sm) -> {
                String allowedSortingColumn = sortableFields.getInternalProperty(sm.getFieldName());
                if (allowedSortingColumn != null) {
                    sortingModelWithColumnNames.add(new SortingModel(sm.getDirection(), allowedSortingColumn));
                }

            });
            return sortingModelWithColumnNames;
        } else {
            return defaultSorting;
        }
    }
}

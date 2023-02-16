package it.pagopa.bs.common.enumeration;

public enum SortingDirection {
    
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private String type;

    private SortingDirection(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}

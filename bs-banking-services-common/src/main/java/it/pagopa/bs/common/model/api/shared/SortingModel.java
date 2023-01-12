package it.pagopa.bs.common.model.api.shared;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import it.pagopa.bs.common.enumeration.SortingDirection;

public class SortingModel {
    
    private @NotNull SortingDirection direction;
    private @NotBlank String fieldName;

    public SortingModel() {
    }

    public SortingModel(SortingDirection direction, String fieldName) {
        this.direction = direction;
        this.fieldName = fieldName;
    }

    public SortingDirection getDirection() {
        return this.direction;
    }

    public void setDirection(SortingDirection direction) {
        this.direction = direction;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SortingModel)) {
            return false;
        } else {
            SortingModel other = (SortingModel)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$direction = this.getDirection();
                Object other$direction = other.getDirection();
                if (this$direction == null) {
                    if (other$direction != null) {
                        return false;
                    }
                } else if (!this$direction.equals(other$direction)) {
                    return false;
                }

                Object this$fieldName = this.getFieldName();
                Object other$fieldName = other.getFieldName();
                if (this$fieldName == null) {
                    if (other$fieldName != null) {
                        return false;
                    }
                } else if (!this$fieldName.equals(other$fieldName)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SortingModel;
    }

    public String toString() {
        return "SortingModel(direction=" + this.getDirection() + ", fieldName=" + this.getFieldName() + ")";
    }
}

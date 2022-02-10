package com.azaitsev.widgets.entity.widget;

import com.azaitsev.widgets.entity.exceptions.InvalidChangeSetException;

import javax.validation.constraints.Min;

/*
    Step builder pattern would fit better here, but I believe it will be an overkill for the scope of this task
 */
final public class WidgetChangeSet {
    private Integer x;
    private Integer y;
    private Integer z;
    private Integer height;
    private Integer width;

    public Widget buildWidget() {
        throwExceptionIfAnyMandatoryFieldIsNull();

        return new Widget(
                x,
                y,
                z,
                height,
                width
        );
    }

    public void applyChanges(Widget widget) {
        if (x != null) {
            widget.setX(x);
        }

        if (y != null) {
            widget.setY(y);
        }

        if (z != null) {
            widget.setZ(z);
        }

        if (height != null) {
            widget.setHeight(height);
        }

        if (width != null) {
            widget.setWidth(width);
        }
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(@Min(1) Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(
            @Min(1) Integer width
    ) {
        this.width = width;
    }

    private void throwExceptionIfAnyMandatoryFieldIsNull() {
        if (x == null) {
            throw new InvalidChangeSetException("Widget's X coordinate is null");
        }

        if (height == null) {
            throw new InvalidChangeSetException("Widget's height is null");
        }

        if (width == null) {
            throw new InvalidChangeSetException("Widget's width is null");
        }
    }
}

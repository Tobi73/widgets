package com.azaitsev.widgets.entity.widget;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Widget implements ImmutableWidget {
    private UUID id;
    private Integer x;
    private Integer y;
    private Integer z;
    private Integer height;
    private Integer width;
    private Instant lastModificationDate;

    public Widget(
            Integer x,
            Integer y,
            Integer z,
            Integer height,
            Integer width
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.height = height;
        this.width = width;
    }

    public Widget(ImmutableWidget widget) {
        this.id = widget.getId();
        this.x = widget.getX();
        this.y = widget.getY();
        this.z = widget.getZ();
        this.height = widget.getHeight();
        this.width = widget.getWidth();
        this.lastModificationDate = widget.getLastModificationDate();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Instant getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Instant lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (!(object instanceof Widget)) {
            return false;
        }

        Widget widgetToCompare = (Widget) object;

        return Objects.equals(this.id, widgetToCompare.id) &&
                Objects.equals(this.x, widgetToCompare.x) &&
                Objects.equals(this.y, widgetToCompare.y) &&
                Objects.equals(this.z, widgetToCompare.z) &&
                Objects.equals(this.height, widgetToCompare.height) &&
                Objects.equals(this.width, widgetToCompare.width) &&
                Objects.equals(this.lastModificationDate, widgetToCompare.lastModificationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                x,
                y,
                z,
                height,
                width,
                lastModificationDate
        );
    }
}

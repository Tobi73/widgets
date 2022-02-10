package com.azaitsev.widgets.controller.v1.widget.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateWidgetRequest {
    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    private Integer z;

    @NotNull
    @Min(1)
    private Integer height;

    @Min(1)
    @NotNull
    private Integer width;

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
}

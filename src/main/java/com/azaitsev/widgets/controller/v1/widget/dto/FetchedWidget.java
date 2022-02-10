package com.azaitsev.widgets.controller.v1.widget.dto;

import com.azaitsev.widgets.entity.widget.ImmutableWidget;

import java.time.Instant;
import java.util.UUID;

public class FetchedWidget {
    private final UUID id;
    private final Integer x;
    private final Integer y;
    private final Integer z;
    private final Integer height;
    private final Integer width;
    private final Instant lastModificationDate;

    public FetchedWidget(ImmutableWidget widget) {
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

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getZ() {
        return z;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public Instant getLastModificationDate() {
        return lastModificationDate;
    }
}

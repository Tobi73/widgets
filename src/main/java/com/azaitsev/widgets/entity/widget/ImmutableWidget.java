package com.azaitsev.widgets.entity.widget;

import java.time.Instant;
import java.util.UUID;

public interface ImmutableWidget {
    UUID getId();

    Integer getX();

    Integer getY();

    Integer getZ();

    Integer getHeight();

    Integer getWidth();

    Instant getLastModificationDate();
}

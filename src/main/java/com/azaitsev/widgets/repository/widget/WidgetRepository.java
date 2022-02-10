package com.azaitsev.widgets.repository.widget;

import com.azaitsev.widgets.entity.widget.ImmutableWidget;
import com.azaitsev.widgets.entity.widget.WidgetChangeSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WidgetRepository {
    Optional<ImmutableWidget> get(UUID widgetId);

    List<ImmutableWidget> getPage(int limit, long offset);

    ImmutableWidget create(WidgetChangeSet widget);

    ImmutableWidget update(UUID widgetId, WidgetChangeSet widget);

    void delete(UUID widgetId);

    void clear();
}

package com.azaitsev.widgets.service.widget;

import com.azaitsev.widgets.entity.widget.ImmutableWidget;
import com.azaitsev.widgets.entity.widget.WidgetChangeSet;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface WidgetService {
    Optional<ImmutableWidget> get(UUID widgetId);

    List<ImmutableWidget> getPage(int limit, long offset);

    ImmutableWidget create(WidgetChangeSet changeSet);

    ImmutableWidget update(UUID widgetId, WidgetChangeSet changeSet);

    void delete(UUID widgetId);
}

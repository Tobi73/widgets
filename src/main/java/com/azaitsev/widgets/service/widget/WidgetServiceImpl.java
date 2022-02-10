package com.azaitsev.widgets.service.widget;

import com.azaitsev.widgets.entity.widget.ImmutableWidget;
import com.azaitsev.widgets.entity.widget.WidgetChangeSet;
import com.azaitsev.widgets.repository.widget.WidgetRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WidgetServiceImpl implements WidgetService {
    private final WidgetRepository widgetRepository;

    public WidgetServiceImpl(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public Optional<ImmutableWidget> get(UUID widgetId) {
        return widgetRepository.get(widgetId);
    }

    public List<ImmutableWidget> getPage(int limit, long offset) {
        return widgetRepository.getPage(limit, offset);
    }

    public ImmutableWidget create(WidgetChangeSet changeSet) {
        return widgetRepository.create(changeSet);
    }

    public ImmutableWidget update(UUID widgetId, WidgetChangeSet changeSet) {
        return widgetRepository.update(widgetId, changeSet);
    }

    public void delete(UUID widgetId) {
        widgetRepository.delete(widgetId);
    }
}

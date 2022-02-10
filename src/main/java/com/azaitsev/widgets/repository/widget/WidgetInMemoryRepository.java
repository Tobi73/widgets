package com.azaitsev.widgets.repository.widget;

import com.azaitsev.widgets.entity.widget.ImmutableWidget;
import com.azaitsev.widgets.entity.widget.Widget;
import com.azaitsev.widgets.entity.widget.WidgetChangeSet;
import com.azaitsev.widgets.repository.exceptions.EntityNotFoundException;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class WidgetInMemoryRepository implements WidgetRepository {
    private final Map<UUID, Widget> widgets;
    private final NavigableMap<Integer, UUID> zAxisOrderIndex;
    private final ReadWriteLock zAxisReorderingLock;

    public WidgetInMemoryRepository() {
        zAxisReorderingLock = new ReentrantReadWriteLock(true);
        widgets = new HashMap<>();
        zAxisOrderIndex = new TreeMap<>();
    }

    public Optional<ImmutableWidget> get(@NotNull UUID widgetId) {
        zAxisReorderingLock.readLock().lock();
        try {
            return Optional.ofNullable(widgets.get(widgetId));
        } finally {
            zAxisReorderingLock.readLock().unlock();
        }
    }

    public List<ImmutableWidget> getPage(int limit, long offset) {
        if (limit < 0) {
            throw new IllegalArgumentException("Page limit  should not be negative");
        }

        if (offset < 0) {
            throw new IllegalArgumentException("Page offset should not be negative");
        }

        zAxisReorderingLock.readLock().lock();
        try {
            return zAxisOrderIndex
                    .values()
                    .stream()
                    .skip(offset)
                    .limit(limit)
                    .map(widgets::get)
                    .collect(Collectors.toList());
        } finally {
            zAxisReorderingLock.readLock().unlock();
        }
    }

    public ImmutableWidget create(@NotNull WidgetChangeSet changeSet) {
        zAxisReorderingLock.writeLock().lock();
        try {
            Widget widget = changeSet.buildWidget();

            // If widget was passed without z index - move new widget to the top
            // by taking current max z index and incrementing it
            Integer zAxisIndex;
            if (widget.getZ() == null) {
                zAxisIndex = getMaxZAxisIndex() + 1;
                widget.setZ(zAxisIndex);
            } else {
                zAxisIndex = widget.getZ();
            }

            // Generate id for new widget
            final var widgetId = getGeneratedWidgetId();
            // Store new widget
            Widget storedWidget = new Widget(widget);
            storedWidget.setId(widgetId);
            storedWidget.setLastModificationDate(Instant.now());

            widgets.put(widgetId, storedWidget);

            // If widget with specified z index already exist - shift it along with other colliding widgets
            if (zAxisOrderIndex.containsKey(zAxisIndex)) {
                shiftZAxis(zAxisIndex);
            }
            zAxisOrderIndex.put(zAxisIndex, widgetId);

            return storedWidget;
        } finally {
            zAxisReorderingLock.writeLock().unlock();
        }
    }

    @Override
    public ImmutableWidget update(@NotNull final UUID widgetId, @NotNull WidgetChangeSet changeSet) {
        zAxisReorderingLock.writeLock().lock();
        try {
            // Get widget that needs to be updates
            final var widget = widgets.get(widgetId);

            // Ensure that requested widget actually exists
            if (widget == null) {
                throw new EntityNotFoundException("Widget with id " + widgetId + " not found");
            }

            // If widget's new z index collides with other widgets - shift other widget along with others
            if (changeSet.getZ() != null) {
                final var newWidgetZIndex = changeSet.getZ();

                zAxisOrderIndex.remove(widget.getZ());
                if (zAxisOrderIndex.containsKey(newWidgetZIndex)) {
                    shiftZAxis(newWidgetZIndex);
                }
                zAxisOrderIndex.put(newWidgetZIndex, widgetId);
            }

            // Update widget in storage
            changeSet.applyChanges(widget);
            widget.setLastModificationDate(Instant.now());

            return widget;
        } finally {
            zAxisReorderingLock.writeLock().unlock();
        }
    }

    @Override
    public void delete(@NotNull UUID widgetId) {
        zAxisReorderingLock.writeLock().lock();
        try {
            final var widgetToDelete = widgets.get(widgetId);

            // Ensure that requested widget actually exists
            if (widgetToDelete == null) {
                throw new EntityNotFoundException("Widget with id " + widgetId + " not found");
            }

            widgets.remove(widgetId);
            zAxisOrderIndex.remove(widgetToDelete.getZ());
        } finally {
            zAxisReorderingLock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        zAxisReorderingLock.writeLock().lock();
        try {
            widgets.clear();
            zAxisOrderIndex.clear();
        } finally {
            zAxisReorderingLock.writeLock().unlock();
        }
    }

    private void shiftZAxis(int shiftFrom) {
        Map<Integer, UUID> widgetsEligibleForShift = zAxisOrderIndex.tailMap(shiftFrom, true);
        Map<Integer, UUID> shiftedWidgets = new TreeMap<>();

        int currentIndex = shiftFrom;
        for (Map.Entry<Integer, UUID> widget : widgetsEligibleForShift.entrySet()) {
            if (widget.getKey() > currentIndex) {
                break;
            }

            int shiftedZIndex = widget.getKey() + 1;
            UUID shiftedWidgetId = widget.getValue();

            shiftedWidgets.put(shiftedZIndex, shiftedWidgetId);
            Widget shiftedWidget = widgets.get(shiftedWidgetId);
            shiftedWidget.setZ(shiftedZIndex);

            currentIndex++;
        }

        zAxisOrderIndex.putAll(shiftedWidgets);
        zAxisOrderIndex.remove(shiftFrom);
    }

    private Integer getMaxZAxisIndex() {
        if (zAxisOrderIndex.isEmpty()) {
            return 0;
        }
        return zAxisOrderIndex.lastKey();
    }

    private UUID getGeneratedWidgetId() {
        return UUID.randomUUID();
    }
}

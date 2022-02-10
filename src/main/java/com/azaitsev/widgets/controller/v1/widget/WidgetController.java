package com.azaitsev.widgets.controller.v1.widget;

import com.azaitsev.widgets.controller.v1.widget.dto.CreateWidgetRequest;
import com.azaitsev.widgets.controller.v1.widget.dto.FetchedWidget;
import com.azaitsev.widgets.controller.v1.widget.dto.UpdateWidgetRequest;
import com.azaitsev.widgets.entity.exceptions.InvalidChangeSetException;
import com.azaitsev.widgets.entity.widget.ImmutableWidget;
import com.azaitsev.widgets.entity.widget.WidgetChangeSet;
import com.azaitsev.widgets.repository.exceptions.EntityNotFoundException;
import com.azaitsev.widgets.service.widget.WidgetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/widgets")
public class WidgetController {
    private static final int MAX_PAGE_LIMIT = 500;
    private static final int DEFAULT_PAGE_LIMIT = 10;
    private static final int DEFAULT_PAGE_OFFSET = 0;

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping(value = "/{widgetId}")
    @ResponseBody
    public FetchedWidget getWidget(@PathVariable UUID widgetId) {
        final var widget = widgetService
                .get(widgetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Widget not found"));

        return new FetchedWidget(widget);
    }

    @GetMapping()
    @ResponseBody
    public List<FetchedWidget> getWidgetsPage(
            @RequestParam(name = "limit", required = false) Integer providedLimit,
            @RequestParam(name = "offset", required = false) Integer providedOffset
    ) {
        int limit = providedLimit != null && providedLimit <= MAX_PAGE_LIMIT && providedLimit > 0
                ? providedLimit
                : DEFAULT_PAGE_LIMIT;
        int offset = providedOffset != null && providedOffset > 0
                ? providedOffset
                : DEFAULT_PAGE_OFFSET;

        final var widgets = widgetService.getPage(limit, offset);

        return widgets.stream().map(FetchedWidget::new).collect(Collectors.toList());
    }

    @PostMapping()
    @ResponseBody
    public FetchedWidget createWidget(@Valid @RequestBody CreateWidgetRequest request) {
        WidgetChangeSet changeSet = new WidgetChangeSet();

        changeSet.setX(request.getX());
        changeSet.setY(request.getY());
        changeSet.setZ(request.getZ());
        changeSet.setHeight(request.getHeight());
        changeSet.setWidth(request.getWidth());

        try {
            ImmutableWidget updatedWidget = widgetService.create(changeSet);
            return new FetchedWidget(updatedWidget);
        } catch (InvalidChangeSetException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid set of changes for widget");
        }
    }

    @PutMapping(value = "/{widgetId}")
    @ResponseBody
    public FetchedWidget updateWidget(
            @Valid @RequestBody UpdateWidgetRequest request,
            @PathVariable UUID widgetId
    ) {
        WidgetChangeSet changeSet = new WidgetChangeSet();

        changeSet.setX(request.getX());
        changeSet.setY(request.getY());
        changeSet.setZ(request.getZ());
        changeSet.setHeight(request.getHeight());
        changeSet.setWidth(request.getWidth());

        try {
            ImmutableWidget updatedWidget = widgetService.update(widgetId, changeSet);
            return new FetchedWidget(updatedWidget);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Widget not found");
        } catch (InvalidChangeSetException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid set of changes for widget");
        }
    }

    @DeleteMapping(value = "/{widgetId}")
    public void deleteWidget(@PathVariable UUID widgetId) {
        try {
            widgetService.delete(widgetId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Widget not found");
        }
    }

}

package com.azaitsev.widgets.repository.widget;

import com.azaitsev.widgets.entity.widget.ImmutableWidget;
import com.azaitsev.widgets.entity.widget.Widget;
import com.azaitsev.widgets.entity.widget.WidgetChangeSet;
import com.azaitsev.widgets.repository.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class WidgetInMemoryRepositoryUnitTest {

    private final WidgetRepository repository = new WidgetInMemoryRepository();

    @AfterEach
    public void tearDown() {
        repository.clear();
    }

    @Test
    public void create_shouldReturnWidgetWithFilledFields() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();

        ImmutableWidget createdWidget = repository.create(changeSet);

        assertNotNull(createdWidget.getId());
        assertNotNull(createdWidget.getLastModificationDate());

        assertEquals(createdWidget.getX(), changeSet.getX());
        assertEquals(createdWidget.getY(), changeSet.getY());
        assertEquals(createdWidget.getZ(), changeSet.getZ());
        assertEquals(createdWidget.getHeight(), changeSet.getHeight());
        assertEquals(createdWidget.getWidth(), changeSet.getWidth());
    }

    @Test
    public void create_shouldMoveWidgetToTheTopIfNoZIndexSpecified() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        changeSet.setZ(null);

        repository.create(changeSet);
        repository.create(changeSet);
        repository.create(changeSet);

        ImmutableWidget topWidget = repository.create(changeSet);

        final Integer expectedZIndex = 4;
        assertNotNull(topWidget.getZ());
        assertEquals(topWidget.getZ(), expectedZIndex);
    }

    @Test
    public void create_shouldShiftWidgetsIfSpecifiedZIndexIsNotVacant() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        changeSet.setZ(null);

        ImmutableWidget firstWidget = repository.create(changeSet);
        ImmutableWidget secondWidget = repository.create(changeSet);
        ImmutableWidget thirdWidget = repository.create(changeSet);

        changeSet.setZ(1);
        ImmutableWidget newWidget = repository.create(changeSet);

        List<ImmutableWidget> createdWidgets = repository.getPage(10, 0);

        int expectedNumOfWidgets = 4;
        List<UUID> expectedOrderOfIds = List.of(
                newWidget.getId(),
                firstWidget.getId(),
                secondWidget.getId(),
                thirdWidget.getId()
        );
        List<Integer> expectedOrderOfZ = List.of(1, 2, 3, 4);

        assertEquals(createdWidgets.size(), expectedNumOfWidgets);
        assertEquals(
                createdWidgets.stream().map(ImmutableWidget::getId).collect(Collectors.toList()),
                expectedOrderOfIds
        );
        assertEquals(
                createdWidgets.stream().map(ImmutableWidget::getZ).collect(Collectors.toList()),
                expectedOrderOfZ
        );
    }

    @Test
    public void create_shouldNotShiftWidgetsIfSpecifiedZIndexIsVacant() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();

        changeSet.setZ(1);
        ImmutableWidget firstWidget = repository.create(changeSet);

        changeSet.setZ(3);
        ImmutableWidget thirdWidget = repository.create(changeSet);

        changeSet.setZ(2);
        ImmutableWidget secondWidget = repository.create(changeSet);

        List<ImmutableWidget> createdWidgets = repository.getPage(10, 0);

        int expectedNumOfWidgets = 3;
        List<UUID> expectedOrderOfIds = List.of(
                firstWidget.getId(),
                secondWidget.getId(),
                thirdWidget.getId()
        );
        List<Integer> expectedOrderOfZ = List.of(1, 2, 3);

        assertEquals(createdWidgets.size(), expectedNumOfWidgets);
        assertEquals(
                createdWidgets.stream().map(ImmutableWidget::getId).collect(Collectors.toList()),
                expectedOrderOfIds
        );
        assertEquals(
                createdWidgets.stream().map(ImmutableWidget::getZ).collect(Collectors.toList()),
                expectedOrderOfZ
        );
    }

    @Test
    public void create_shouldNotShiftZIndexesIfGapIsAvailable() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        changeSet.setZ(1);
        ImmutableWidget createdFirstWidget = repository.create(changeSet);

        changeSet.setZ(3);
        ImmutableWidget createdSecondWidget = repository.create(changeSet);

        changeSet.setZ(2);
        ImmutableWidget createdWidget = repository.create(changeSet);

        final Integer expectedZIndexOfCreatedWidget = 2;
        assertEquals(createdWidget.getZ(), expectedZIndexOfCreatedWidget);

        final Integer expectedZIndexOfFirstWidget = 1;
        Optional<ImmutableWidget> fetchResult = repository.get(createdFirstWidget.getId());
        assertTrue(fetchResult.isPresent());

        ImmutableWidget fetchedFirstWidget = fetchResult.get();
        assertEquals(fetchedFirstWidget.getZ(), expectedZIndexOfFirstWidget);

        final Integer expectedZIndexOfSecondWidget = 3;
        fetchResult = repository.get(createdSecondWidget.getId());
        assertTrue(fetchResult.isPresent());

        ImmutableWidget fetchedSecondWidget = fetchResult.get();
        assertEquals(fetchedSecondWidget.getZ(), expectedZIndexOfSecondWidget);
    }

    @Test
    public void create_shouldMoveWidgetToTheTopWithOtherWidgetsHavingNegativeZIndex() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        changeSet.setZ(-10);
        repository.create(changeSet);

        changeSet.setZ(-11);
        repository.create(changeSet);

        changeSet.setZ(null);
        ImmutableWidget topWidget = repository.create(changeSet);

        final Integer expectedZIndex = -9;
        assertNotNull(topWidget);
        assertNotNull(topWidget.getZ());
        assertEquals(topWidget.getZ(), expectedZIndex);
    }

    @Test
    public void createAndGet_shouldCreateWidgetAndReturnItById() {
        ImmutableWidget createdWidget = repository.create(getDefaultWidgetChangeSet());
        Optional<ImmutableWidget> fetchResult = repository.get(createdWidget.getId());
        assertTrue(fetchResult.isPresent());

        ImmutableWidget fetchedWidget = fetchResult.get();
        assertNotNull(fetchedWidget);
        assertNotNull(fetchedWidget.getId());
        assertNotNull(fetchedWidget.getZ());
        assertEquals(createdWidget, fetchedWidget);
    }

    @Test
    public void update_shouldUpdateSpecifiedFields() throws InterruptedException {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();

        // Small hack for dereferencing widget =[ Maybe I should deference it at repository level?
        ImmutableWidget createdWidget = new Widget(repository.create(changeSet));

        WidgetChangeSet updateChangeSet = new WidgetChangeSet();
        updateChangeSet.setX(111);
        updateChangeSet.setY(222);
        updateChangeSet.setWidth(333);

        // Emulate time span to check the change in lastModificationDate
        TimeUnit.MILLISECONDS.sleep(100);
        repository.update(createdWidget.getId(), updateChangeSet);

        Optional<ImmutableWidget> optionalFetchedUpdatedWidget = repository.get(createdWidget.getId());
        assertTrue(optionalFetchedUpdatedWidget.isPresent());
        ImmutableWidget fetchedUpdatedWidget = optionalFetchedUpdatedWidget.get();

        assertEquals(fetchedUpdatedWidget.getId(), createdWidget.getId());
        assertEquals(111, (int) fetchedUpdatedWidget.getX());
        assertEquals(222, (int) fetchedUpdatedWidget.getY());
        assertEquals(333, (int) fetchedUpdatedWidget.getWidth());
        assertEquals(createdWidget.getHeight(), fetchedUpdatedWidget.getHeight());
        assertTrue(
                fetchedUpdatedWidget
                        .getLastModificationDate()
                        .isAfter(createdWidget.getLastModificationDate())
        );
    }

    @Test
    public void update_shouldShiftWidgetsIfSpecifiedZIsNotVacant() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        changeSet.setZ(null);

        ImmutableWidget firstWidget = repository.create(changeSet);
        ImmutableWidget secondWidget = repository.create(changeSet);
        ImmutableWidget thirdWidget = repository.create(changeSet);

        changeSet.setZ(1);
        repository.update(thirdWidget.getId(), changeSet);

        List<ImmutableWidget> createdWidgets = repository.getPage(10, 0);

        int expectedNumOfWidgets = 3;
        List<UUID> expectedOrderOfIds = List.of(
                thirdWidget.getId(),
                firstWidget.getId(),
                secondWidget.getId()
        );
        List<Integer> expectedOrderOfZ = List.of(1, 2, 3);

        assertEquals(createdWidgets.size(), expectedNumOfWidgets);
        assertEquals(
                createdWidgets.stream().map(ImmutableWidget::getId).collect(Collectors.toList()),
                expectedOrderOfIds
        );
        assertEquals(
                createdWidgets.stream().map(ImmutableWidget::getZ).collect(Collectors.toList()),
                expectedOrderOfZ
        );
    }

    @Test
    public void update_shouldNotShiftWidgetsIfSpecifiedZIsVacant() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        changeSet.setZ(null);

        // Yet another dereference hack
        ImmutableWidget firstWidget = new Widget(repository.create(changeSet));
        ImmutableWidget secondWidget = new Widget(repository.create(changeSet));
        ImmutableWidget thirdWidget = new Widget(repository.create(changeSet));

        changeSet.setZ(4);
        repository.update(thirdWidget.getId(), changeSet);

        List<ImmutableWidget> createdWidgets = repository.getPage(10, 0);

        int expectedNumOfWidgets = 3;
        List<UUID> expectedOrderOfIds = List.of(
                firstWidget.getId(),
                secondWidget.getId(),
                thirdWidget.getId()
        );
        List<Integer> expectedOrderOfZ = List.of(1, 2, 4);

        assertEquals(createdWidgets.size(), expectedNumOfWidgets);
        assertEquals(
                createdWidgets.stream().map(ImmutableWidget::getId).collect(Collectors.toList()),
                expectedOrderOfIds
        );
        assertEquals(
                createdWidgets.stream().map(ImmutableWidget::getZ).collect(Collectors.toList()),
                expectedOrderOfZ
        );
    }

    @Test
    public void update_shouldThrowExceptionIfWidgetWithSpecifiedIdDoesntExists() {
        WidgetChangeSet changeSet = new WidgetChangeSet();
        UUID widgetId = UUID.randomUUID();

        assertThrows(EntityNotFoundException.class, () -> {
            repository.update(widgetId, changeSet);
        });
    }

    @Test
    public void getPage_shouldReturnPageOfWidgetsOrderedByZIndex() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();

        changeSet.setZ(-1);
        repository.create(changeSet);

        changeSet.setZ(5);
        repository.create(changeSet);

        changeSet.setZ(0);
        repository.create(changeSet);

        changeSet.setZ(null);
        repository.create(changeSet);

        List<ImmutableWidget> pageOfWidgets = repository.getPage(10, 0);

        int expectedPageSize = 4;
        List<Integer> expectedZIndexOrder = List.of(-1, 0, 5, 6);

        assertEquals(pageOfWidgets.size(), expectedPageSize);
        assertEquals(
                pageOfWidgets.stream().map(ImmutableWidget::getZ).collect(Collectors.toList()),
                expectedZIndexOrder
        );
    }

    @Test
    public void getPage_shouldReturnPageWithSpecifiedLimit() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        changeSet.setZ(null);

        ImmutableWidget firstWidget = repository.create(changeSet);

        repository.create(changeSet);

        List<ImmutableWidget> widgetsPage = repository.getPage(1, 0);

        int expectedPageSize = 1;
        UUID expectedFirstWidgetId = firstWidget.getId();

        assertEquals(widgetsPage.size(), expectedPageSize);

        Optional<ImmutableWidget> optionalFirstWidget = widgetsPage.stream().findFirst();
        assertTrue(optionalFirstWidget.isPresent());

        UUID firstWidgetId = optionalFirstWidget.get().getId();
        assertEquals(firstWidgetId, expectedFirstWidgetId);
    }

    @Test
    public void getPage_shouldThrowExceptionIfNegativeLimitProvided() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.getPage(-10, 0);
        });
    }

    @Test
    public void getPage_shouldReturnPageWithSpecifiedOffset() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        changeSet.setZ(null);

        repository.create(changeSet);

        ImmutableWidget secondWidget = repository.create(changeSet);

        List<ImmutableWidget> widgetsPage = repository.getPage(10, 1);

        int expectedPageSize = 1;
        UUID expectedSecondWidgetId = secondWidget.getId();

        assertEquals(widgetsPage.size(), expectedPageSize);

        Optional<ImmutableWidget> optionalSecondWidget = widgetsPage.stream().findFirst();
        assertTrue(optionalSecondWidget.isPresent());

        UUID secondWidgetId = optionalSecondWidget.get().getId();
        assertEquals(secondWidgetId, expectedSecondWidgetId);
    }

    @Test
    public void getPage_shouldThrowExceptionIfNegativeOffsetProvided() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.getPage(10, -1);
        });
    }

    @Test
    public void delete_shouldDeleteWidgetById() {
        WidgetChangeSet changeSet = getDefaultWidgetChangeSet();
        ImmutableWidget widget = repository.create(changeSet);

        // Ensure that widget was actually created
        Optional<ImmutableWidget> foundWidget = repository.get(widget.getId());
        assertTrue(foundWidget.isPresent());

        repository.delete(widget.getId());
        Optional<ImmutableWidget> deletedWidget = repository.get(widget.getId());
        assertFalse(deletedWidget.isPresent());
    }

    @Test
    public void delete_shouldThrowExceptionIfIdOfNonExistentWidget() {
        UUID idOfNonExistentWidget = UUID.randomUUID();

        assertThrows(EntityNotFoundException.class, () -> {
            repository.delete(idOfNonExistentWidget);
        });
    }

    private WidgetChangeSet getDefaultWidgetChangeSet() {
        WidgetChangeSet changeSet = new WidgetChangeSet();

        changeSet.setX(10);
        changeSet.setY(20);
        changeSet.setZ(30);
        changeSet.setHeight(100);
        changeSet.setWidth(200);

        return changeSet;
    }
}

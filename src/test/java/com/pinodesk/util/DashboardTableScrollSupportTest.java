package com.pinodesk.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import org.junit.jupiter.api.Test;

import com.pinodesk.JavaFXTestBase;

class DashboardTableScrollSupportTest extends JavaFXTestBase {

    @Test
    void installAddsScrollActiveStyleClass() {
        ScrollPane page = new ScrollPane();
        TableView<String> table = new TableView<>();

        DashboardTableScrollSupport.install(page, List.of(table));

        assertTrue(table.getStyleClass().contains("dashboard-scroll-table"));
    }

    @Test
    void installHandlesMultipleTables() {
        ScrollPane page = new ScrollPane();
        TableView<String> table1 = new TableView<>();
        TableView<String> table2 = new TableView<>();
        page.setContent(new VBox(table1, table2));

        DashboardTableScrollSupport.install(page, List.of(table1, table2));

        assertTrue(table1.getStyleClass().contains("dashboard-scroll-table"));
        assertTrue(table2.getStyleClass().contains("dashboard-scroll-table"));
    }

    @Test
    void installHandlesEmptyTableList() {
        ScrollPane page = new ScrollPane();

        assertDoesNotThrow(() -> DashboardTableScrollSupport.install(page, List.of()));
    }
}

package com.pinodesk.util;

import java.util.List;

import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/** Keeps nested tables from capturing page scrolling before the user engages them. */
public final class DashboardTableScrollSupport {
    private static final PseudoClass ACTIVE = PseudoClass.getPseudoClass("scroll-active");
    private final List<TableView<?>> tables;
    private TableView<?> active;

    private DashboardTableScrollSupport(List<TableView<?>> tables) {
        this.tables = tables;
    }

    public static void install(ScrollPane page, List<TableView<?>> tables) {
        DashboardTableScrollSupport support = new DashboardTableScrollSupport(tables);
        page.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> support.activate(support.tableFor(event.getTarget())));
        page.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE && support.active != null) {
                support.activate(null);
                page.requestFocus();
                event.consume();
            }
        });
        for (TableView<?> table : tables) {
            table.getStyleClass().add("dashboard-scroll-table");
            table.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                if (support.active == table) {
                    support.activate(null);
                }
            });
            table.focusedProperty().addListener((observable, oldValue, focused) -> {
                if (focused) {
                    support.activate(table); // Includes keyboard Tab navigation.
                }
            });
        }
        page.addEventFilter(ScrollEvent.SCROLL, event -> {
            TableView<?> table = support.tableFor(event.getTarget());
            if (table != null && (support.active != table || atVerticalEdge(table, event))) {
                // Retarget above the table's VirtualFlow so only the page consumes the wheel gesture.
                Node content = page.getContent();
                ScrollEvent forwarded = event.copyFor(content, content);
                event.consume();
                content.fireEvent(forwarded);
            }
        });
    }

    private TableView<?> tableFor(Object target) {
        for (Node node = target instanceof Node n ? n : null; node != null; node = node.getParent()) {
            if (node instanceof TableView<?> table && tables.contains(table)) {
                return table;
            }
        }
        return null;
    }

    private void activate(TableView<?> table) {
        if (active != null) {
            active.pseudoClassStateChanged(ACTIVE, false);
        }
        active = table;
        if (active != null) {
            active.pseudoClassStateChanged(ACTIVE, true);
        }
    }

    private static boolean atVerticalEdge(TableView<?> table, ScrollEvent event) {
        if (event.getDeltaY() == 0 || Math.abs(event.getDeltaX()) > Math.abs(event.getDeltaY())) {
            return false;
        }
        for (Node node : table.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar bar && bar.getOrientation() == Orientation.VERTICAL && bar.isVisible()) {
                return event.getDeltaY() > 0 ? bar.getValue() <= bar.getMin()
                        : bar.getValue() >= bar.getMax();
            }
        }
        return true;
    }
}

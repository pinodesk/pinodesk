package com.pinodesk.util;

import java.util.List;
import java.util.ResourceBundle;

import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.service.ExpenseCategoryService;
import com.pinodesk.service.SessionService;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public final class ExpenseCategoryControls {
    private ExpenseCategoryControls() {
    }

    public static boolean canWrite(SessionService session) {
        return session.isCurrentSessionActive() && session.getCurrentSession().getUserGroupMenus().stream().anyMatch(
                menu -> MenuCodeConstants.SETTINGS_EXPENSE_CATEGORIES.equals(menu.getMenuCode())
                        && SimpleStatus.YES.toString().equals(menu.getWrite()));
    }

    public static void initialize(ComboBox<ExpenseCategory> box, ResourceBundle resources, Long selectedId) {
        initialize(box, resources, selectedId, false);
    }

    public static void initialize(
            ComboBox<ExpenseCategory> box,
            ResourceBundle resources,
            Long selectedId,
            boolean allowAll) {
        ExpenseCategoryService service = SpringUtils.getBean(ExpenseCategoryService.class);
        List<ExpenseCategory> categories = service.findAll().stream()
                .filter(c -> UserStatus.ACTIVE.toString().equals(c.getStatus())).toList();
        box.getItems().clear();
        if (allowAll) {
            box.getItems().add(null);
        }
        box.getItems().addAll(categories);
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(ExpenseCategory category) {
                return category == null ? "" : category.getName();
            }

            @Override
            public ExpenseCategory fromString(String text) {
                return null;
            }
        });
        if (allowAll) {
            if (selectedId == null) {
                box.getSelectionModel().selectFirst();
                return;
            }
        }
        if (selectedId != null) {
            box.getItems().stream().filter(c -> c != null).filter(c -> selectedId.equals(c.getId())).findFirst()
                    .ifPresent(box::setValue);
        } else if (!box.getItems().isEmpty()) {
            box.getSelectionModel().selectFirst();
        }
    }
}

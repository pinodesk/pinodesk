package com.pinodesk.util;

import java.util.List;
import java.util.ResourceBundle;

import com.pinodesk.constant.MenuCodeConstants;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.PaymentMethod;
import com.pinodesk.service.PaymentMethodService;
import com.pinodesk.service.SessionService;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;

public final class PaymentMethodControls {
    public static boolean canWrite(SessionService session) {
        return session.isCurrentSessionActive() && session.getCurrentSession().getUserGroupMenus().stream().anyMatch(
                menu -> MenuCodeConstants.SETTINGS_PAYMENT_METHODS.equals(menu.getMenuCode())
                        && SimpleStatus.YES.toString().equals(menu.getWrite()));
    }

    private PaymentMethodControls() {
    }

    public static void initialize(ComboBox<PaymentMethod> box, ResourceBundle resources, Long selectedId) {
        initialize(box, resources, selectedId, false);
    }

    public static void initialize(
            ComboBox<PaymentMethod> box,
            ResourceBundle resources,
            Long selectedId,
            boolean allowAll) {
        PaymentMethodService service = SpringUtils.getBean(PaymentMethodService.class);
        box.getItems().setAll(service.findActive());
        box.getItems().add(0, null);
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(PaymentMethod method) {
                return method == null ? "" : method.getName();
            }

            @Override
            public PaymentMethod fromString(String text) {
                return null;
            }
        });
        if (allowAll) {
            box.setValue(null);
            if (selectedId == null)
                return;
        }
        box.getItems().stream().filter(m -> m != null)
                .filter(m -> selectedId == null ? m.isDefaultMethod() : selectedId.equals(m.getId())).findFirst()
                .ifPresent(box::setValue);
    }

    /** Visible, keyboard-accessible choices with exactly one selected method. */
    public static ToggleGroup initializeChoices(FlowPane pane, List<PaymentMethod> methods) {
        ToggleGroup group = new ToggleGroup();
        pane.getChildren().clear();
        for (PaymentMethod method : methods.stream()
                .filter(method -> UserStatus.ACTIVE.toString().equals(method.getStatus())).toList()) {
            ToggleButton button = new ToggleButton(method.getName());
            button.setUserData(method);
            button.setToggleGroup(group);
            button.getStyleClass().add("payment-method-choice");
            button.setPrefWidth(124);
            button.setMinHeight(38);
            button.setTooltip(new Tooltip(method.getName()));
            pane.getChildren().add(button);
        }
        return group;
    }

    public static PaymentMethod selectedMethod(ToggleGroup group) {
        return group.getSelectedToggle() == null ? null : (PaymentMethod) group.getSelectedToggle().getUserData();
    }
}

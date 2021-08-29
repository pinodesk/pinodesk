package toscabox.desktop.viewmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PurchaseFilterVM {
    private String orderNumber;
    private LocalDate orderDateMin;
    private LocalDate orderDateMax;
}

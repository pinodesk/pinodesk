package pospino.desktop.viewmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PayableClosestDueDateVM {
    private String supplierName;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
}

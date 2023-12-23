package pinodesk.viewmodel;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReceivableClosestDueDateVM {
    private String customerName;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
}

package pinus.desktop.viewmodel;

import java.util.List;

import lombok.Data;

@Data
public class PayableEditVM {
    private List<PayablePaymentVM> payments;
}

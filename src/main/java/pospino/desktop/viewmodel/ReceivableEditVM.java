package pospino.desktop.viewmodel;

import java.util.List;

import lombok.Data;

@Data
public class ReceivableEditVM {
    private List<ReceivablePaymentVM> payments;
}

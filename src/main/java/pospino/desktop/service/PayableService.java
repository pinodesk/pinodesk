package pospino.desktop.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pospino.desktop.annotation.ForActivity;
import pospino.desktop.constant.Activity;
import pospino.desktop.constant.CacheNameConstants;
import pospino.desktop.constant.DomainError;
import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.domain.Payable;
import pospino.desktop.domain.PayablePayment;
import pospino.desktop.domain.Purchase;
import pospino.desktop.exception.DomainException;
import pospino.desktop.repository.PayablePaymentRepository;
import pospino.desktop.repository.PayableRepository;
import pospino.desktop.repository.PurchaseRepository;
import pospino.desktop.viewmodel.PayableEditVM;
import pospino.desktop.viewmodel.PayableFilterVM;
import pospino.desktop.viewmodel.PayablePaymentVM;
import pospino.desktop.viewmodel.PayableVM;

@Service
public class PayableService extends BaseService {

    @Autowired
    private PayableRepository payableRepository;

    @Autowired
    private PayablePaymentRepository payablePaymentRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @ForActivity(Activity.SEARCH_PAYABLES_BY_FILTER)
    @Cacheable(CacheNameConstants.PAYABLES_BY_FILTER)
    public List<PayableVM> searchPayables(PayableFilterVM filter) {
        return payableRepository.findByFilter(filter);
    }

    @ForActivity(Activity.EDIT_PAYABLE)
    @CacheEvict(value = { CacheNameConstants.PAYABLES_BY_FILTER, CacheNameConstants.PURCHASES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void updatePayable(PayableEditVM payableEdit, Long payableId) {
        Payable payable = payableRepository.findById(payableId)
                .orElseThrow(() -> new DomainException(DomainError.PAYABLE_NOT_FOUND_BY_ID));
        payablePaymentRepository.deleteByPayableId(payableId);
        List<PayablePayment> payments = new ArrayList<>();
        LocalDate maxPaymentDate = null;
        BigDecimal total = BigDecimal.ZERO;
        for (PayablePaymentVM vm : payableEdit.getPayments()) {
            total = total.add(vm.getAmount());
            if (total.compareTo(payable.getAmount()) > 0) {
                throw new DomainException(DomainError.PAYMENT_AMOUNT_GREATER_THAN_PAYABLE_AMOUNT);
            }
            PayablePayment payment = new PayablePayment();
            payment.setAmount(vm.getAmount());
            payment.setPayableId(payableId);
            payment.setPaymentDate(vm.getPaymentDate());
            payments.add(payment);
            if (maxPaymentDate == null || maxPaymentDate.isBefore(vm.getPaymentDate())) {
                maxPaymentDate = vm.getPaymentDate();
            }
        }
        Purchase purchase = purchaseRepository.findByIdAndDeletedAtIsNull(payable.getPurchaseId()).orElseThrow();
        if (total.compareTo(payable.getAmount()) == 0) {
            payable.setCompletionDate(maxPaymentDate);
            purchase.setPaymentStatus(PaymentStatus.PAID.toString());
            purchase.setPaymentDueDate(null);
        } else {
            payable.setCompletionDate(null);
            purchase.setPaymentStatus(PaymentStatus.UNPAID.toString());
            purchase.setPaymentDueDate(payable.getDueDate());
        }
        purchaseRepository.save(purchase);
        payablePaymentRepository.saveAll(payments);
        payableRepository.save(payable);
    }

    @ForActivity(Activity.GET_PAYABLE_PAYMENTS)
    public List<PayablePaymentVM> getPayablePayments(Long payableId) {
        return objectConverter.convertList(
                payablePaymentRepository.findByPayableIdAndDeletedAtIsNull(payableId),
                PayablePaymentVM.class);
    }

}

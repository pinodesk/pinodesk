package pinus.desktop.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.annotation.ForActivity;
import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.Payable;
import pinus.desktop.domain.PayablePayment;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.PayablePaymentRepository;
import pinus.desktop.repository.PayableRepository;
import pinus.desktop.viewmodel.PayableEditVM;
import pinus.desktop.viewmodel.PayableFilterVM;
import pinus.desktop.viewmodel.PayablePaymentVM;
import pinus.desktop.viewmodel.PayableVM;

@Service
public class PayableService extends BaseService {

    @Autowired
    private PayableRepository payableRepository;

    @Autowired
    private PayablePaymentRepository payablePaymentRepository;

    @ForActivity(Activity.SEARCH_PAYABLES_BY_FILTER)
    @Cacheable(CacheNameConstants.PAYABLES_BY_FILTER)
    public List<PayableVM> searchPayables(PayableFilterVM filter) {
        return payableRepository.findByFilter(filter);
    }

    @ForActivity(Activity.EDIT_PAYABLE)
    @CacheEvict(value = { CacheNameConstants.PAYABLES_BY_FILTER }, allEntries = true)
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
        if (total.compareTo(payable.getAmount()) == 0) {
            payable.setCompletionDate(maxPaymentDate);
        } else {
            payable.setCompletionDate(null);
        }
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

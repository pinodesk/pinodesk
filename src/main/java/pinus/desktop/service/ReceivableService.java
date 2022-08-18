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
import pinus.desktop.constant.PaymentStatus;
import pinus.desktop.domain.Receivable;
import pinus.desktop.domain.ReceivablePayment;
import pinus.desktop.domain.Sale;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.ReceivablePaymentRepository;
import pinus.desktop.repository.ReceivableRepository;
import pinus.desktop.repository.SaleRepository;
import pinus.desktop.viewmodel.ReceivableEditVM;
import pinus.desktop.viewmodel.ReceivableFilterVM;
import pinus.desktop.viewmodel.ReceivablePaymentVM;
import pinus.desktop.viewmodel.ReceivableVM;

@Service
public class ReceivableService extends BaseService {

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private ReceivablePaymentRepository receivablePaymentRepository;

    @Autowired
    private SaleRepository saleRepository;

    @ForActivity(Activity.SEARCH_RECEIVABLES_BY_FILTER)
    @Cacheable(CacheNameConstants.RECEIVABLES_BY_FILTER)
    public List<ReceivableVM> searchReceivables(ReceivableFilterVM filter) {
        return receivableRepository.findByFilter(filter);
    }

    @ForActivity(Activity.EDIT_RECEIVABLE)
    @CacheEvict(value = { CacheNameConstants.RECEIVABLES_BY_FILTER, CacheNameConstants.SALES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void updateReceivable(ReceivableEditVM receivableEdit, Long receivableId) {
        Receivable receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() -> new DomainException(DomainError.RECEIVABLE_NOT_FOUND_BY_ID));
        receivablePaymentRepository.deleteByReceivableId(receivableId);
        List<ReceivablePayment> payments = new ArrayList<>();
        LocalDate maxPaymentDate = null;
        BigDecimal total = BigDecimal.ZERO;
        for (ReceivablePaymentVM vm : receivableEdit.getPayments()) {
            total = total.add(vm.getAmount());
            if (total.compareTo(receivable.getAmount()) > 0) {
                throw new DomainException(DomainError.PAYMENT_AMOUNT_GREATER_THAN_RECEIVABLE_AMOUNT);
            }
            ReceivablePayment payment = new ReceivablePayment();
            payment.setAmount(vm.getAmount());
            payment.setReceivableId(receivableId);
            payment.setPaymentDate(vm.getPaymentDate());
            payments.add(payment);
            if (maxPaymentDate == null || maxPaymentDate.isBefore(vm.getPaymentDate())) {
                maxPaymentDate = vm.getPaymentDate();
            }
        }
        Sale sale = saleRepository.findByIdAndDeletedAtIsNull(receivable.getSaleId()).orElseThrow();
        if (total.compareTo(receivable.getAmount()) == 0) {
            receivable.setCompletionDate(maxPaymentDate);
            sale.setPaymentStatus(PaymentStatus.PAID.toString());
        } else {
            receivable.setCompletionDate(null);
            sale.setPaymentStatus(PaymentStatus.UNPAID.toString());
        }
        saleRepository.save(sale);
        receivablePaymentRepository.saveAll(payments);
        receivableRepository.save(receivable);
    }

    @ForActivity(Activity.GET_RECEIVABLE_PAYMENTS)
    public List<ReceivablePaymentVM> getReceivablePayments(Long receivableId) {
        return objectConverter.convertList(
                receivablePaymentRepository.findByReceivableIdAndDeletedAtIsNull(receivableId),
                ReceivablePaymentVM.class);
    }

}

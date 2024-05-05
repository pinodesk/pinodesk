package pinodesk.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.annotation.ForActivity;
import pinodesk.constant.Activity;
import pinodesk.constant.CacheNameConstants;
import pinodesk.constant.DomainError;
import pinodesk.constant.PaymentStatus;
import pinodesk.entity.Receivable;
import pinodesk.entity.ReceivablePayment;
import pinodesk.entity.Sale;
import pinodesk.exception.DomainException;
import pinodesk.repository.SaleRepository;
import pinodesk.viewmodel.ReceivableEditVM;
import pinodesk.viewmodel.ReceivableFilterVM;
import pinodesk.viewmodel.ReceivablePaymentVM;
import pinodesk.viewmodel.ReceivableVM;
import pinodesk.repository.ReceivablePaymentRepository;
import pinodesk.repository.ReceivableRepository;

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
            sale.setPaymentDueDate(null);
        } else {
            receivable.setCompletionDate(null);
            sale.setPaymentStatus(PaymentStatus.UNPAID.toString());
            sale.setPaymentDueDate(receivable.getDueDate());
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

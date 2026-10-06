package com.pinodesk.service;

import java.util.List;
import java.util.Objects;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.constant.Activity;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.constant.PaymentMethodCategory;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.PaymentMethod;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.PaymentMethodRepository;
import com.pinodesk.repository.SaleRepository;

@Service
public class PaymentMethodService {
    private final PaymentMethodRepository repository;
    private final SaleRepository sales;

    public PaymentMethodService(PaymentMethodRepository repository, SaleRepository sales) {
        this.repository = repository;
        this.sales = sales;
    }

    @TargetActivity(Activity.GET_PAYMENT_METHODS)
    public List<PaymentMethod> findAll() {
        return repository.findAllByOrderByDefaultMethodDescNameAsc();
    }

    public List<PaymentMethod> findActive() {
        return repository
                .findByStatusOrderByDefaultMethodDescNameAsc(com.pinodesk.constant.UserStatus.ACTIVE.toString());
    }

    @TargetActivity(Activity.GET_PAYMENT_METHOD)
    public PaymentMethod get(Long id) {
        return (id == null ? repository.findByDefaultMethodTrue() : repository.findById(id))
                .orElseThrow(() -> new DomainException(DomainError.PAYMENT_METHOD_NOT_FOUND));
    }

    @Transactional
    @CacheEvict(value = CacheNameConstants.SALES_BY_FILTER, allEntries = true)
    @TargetActivity(Activity.SAVE_PAYMENT_METHOD)
    public PaymentMethod save(Long id, String name, PaymentMethodCategory category) {
        return save(
                id,
                name,
                category,
                id == null ? UserStatus.ACTIVE : UserStatus.valueOf(get(id).getStatus().toUpperCase()));
    }

    @Transactional
    @CacheEvict(value = CacheNameConstants.SALES_BY_FILTER, allEntries = true)
    @TargetActivity(Activity.SAVE_PAYMENT_METHOD)
    public PaymentMethod save(Long id, String name, PaymentMethodCategory category, UserStatus status) {
        if (name == null || name.isBlank() || name.trim().length() > 100 || category == null) {
            throw new DomainException(DomainError.PAYMENT_METHOD_INVALID);
        }
        String cleaned = name.trim();
        repository.findByNameIgnoreCase(cleaned).ifPresent(existing -> {
            if (!Objects.equals(existing.getId(), id))
                throw new DomainException(DomainError.PAYMENT_METHOD_DUPLICATE);
        });
        PaymentMethod method = id == null ? new PaymentMethod() : get(id);
        if (method.isDefaultMethod() && category != PaymentMethodCategory.CASH) {
            throw new DomainException(DomainError.PAYMENT_METHOD_PROTECTED);
        }
        method.setName(cleaned);
        method.setCategory(category.name());
        method.setStatus(status.toString());
        try {
            return repository.save(method);
        } catch (DataIntegrityViolationException ex) {
            throw new DomainException(DomainError.PAYMENT_METHOD_DUPLICATE);
        }
    }

    @Transactional
    @TargetActivity(Activity.REMOVE_PAYMENT_METHODS)
    public void remove(List<Long> ids) {
        List<PaymentMethod> methods = ids.stream().distinct().map(this::get).toList();
        for (PaymentMethod method : methods) {
            if (method.isDefaultMethod())
                throw new DomainException(DomainError.PAYMENT_METHOD_PROTECTED);
            if (sales.existsByPaymentMethodId(method.getId()))
                throw new DomainException(DomainError.PAYMENT_METHOD_IN_USE);
        }
        try {
            repository.deleteAll(methods);
        } catch (DataIntegrityViolationException ex) {
            throw new DomainException(DomainError.PAYMENT_METHOD_IN_USE);
        }
    }
}

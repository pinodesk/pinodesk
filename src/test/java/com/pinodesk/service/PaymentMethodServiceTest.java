package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pinodesk.constant.PaymentMethodCategory;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.PaymentMethod;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.PaymentMethodRepository;
import com.pinodesk.repository.SaleRepository;

class PaymentMethodServiceTest {
    PaymentMethodRepository repository = mock(PaymentMethodRepository.class);
    SaleRepository sales = mock(SaleRepository.class);
    PaymentMethodService service = new PaymentMethodService(repository, sales);
    PaymentMethod cash;

    @BeforeEach
    void setup() {
        cash = method(1L, "Cash", true);
        when(repository.findById(1L)).thenReturn(Optional.of(cash));
        when(repository.findByDefaultMethodTrue()).thenReturn(Optional.of(cash));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private PaymentMethod method(Long id, String name, boolean isDefault) {
        PaymentMethod m = new PaymentMethod();
        m.setId(id);
        m.setName(name);
        m.setCategory("CASH");
        m.setDefaultMethod(isDefault);
        return m;
    }

    @Test
    void defaultCanBeRenamedAndStillResolved() {
        assertEquals("Tunai", service.save(1L, "  Tunai  ", PaymentMethodCategory.CASH).getName());
        assertSame(cash, service.get(null));
        assertThrows(DomainException.class, () -> service.save(1L, "Tunai", PaymentMethodCategory.TRANSFER));
        assertThrows(DomainException.class, () -> service.remove(List.of(1L)));
        verify(repository, never()).deleteAll(any(Iterable.class));
    }

    @Test
    void createsAllFourCategoriesAndValidatesNames() {
        for (PaymentMethodCategory category : PaymentMethodCategory.values()) {
            PaymentMethod m = service.save(null, " Test " + category + " ", category);
            assertEquals(category.name(), m.getCategory());
            assertFalse(m.isDefaultMethod());
        }
        assertThrows(DomainException.class, () -> service.save(null, " ", PaymentMethodCategory.CASH));
        assertThrows(DomainException.class, () -> service.save(null, "x".repeat(101), PaymentMethodCategory.CASH));
        assertThrows(DomainException.class, () -> service.save(null, "BCA", null));
        when(repository.findByNameIgnoreCase("cash")).thenReturn(Optional.of(cash));
        assertThrows(DomainException.class, () -> service.save(null, " cash ", PaymentMethodCategory.CASH));
    }

    @Test
    void validatesEntireSelectionBeforeDeletingAndPreservesHistory() {
        PaymentMethod other = method(2L, "Other", false);
        when(repository.findById(2L)).thenReturn(Optional.of(other));
        assertThrows(DomainException.class, () -> service.remove(List.of(2L, 1L)));
        verify(repository, never()).deleteAll(any(Iterable.class));
        when(sales.existsByPaymentMethodId(2L)).thenReturn(true);
        assertThrows(DomainException.class, () -> service.remove(List.of(2L)));
        verify(repository, never()).deleteAll(any(Iterable.class));
        when(sales.existsByPaymentMethodId(2L)).thenReturn(false);
        service.remove(List.of(2L));
        verify(repository).deleteAll(List.of(other));
    }

    @Test
    void missingSelectionIdIsRejected() {
        assertThrows(DomainException.class, () -> service.get(999L));
    }

    @Test
    void findAllReturnsAllMethodsOrdered() {
        PaymentMethod cash = method(1L, "Cash", true);
        PaymentMethod transfer = method(2L, "Transfer", false);
        PaymentMethod credit = method(3L, "Credit", false);

        when(repository.findAllByOrderByDefaultMethodDescNameAsc()).thenReturn(List.of(cash, transfer, credit));

        List<PaymentMethod> result = service.findAll();

        assertEquals(3, result.size());
        assertEquals(cash, result.get(0));
        assertEquals(transfer, result.get(1));
        assertEquals(credit, result.get(2));
        verify(repository).findAllByOrderByDefaultMethodDescNameAsc();
    }

    @Test
    void findActiveReturnsOnlyActiveMethodsOrdered() {
        PaymentMethod cash = method(1L, "Cash", true);
        cash.setStatus(UserStatus.ACTIVE.toString());
        PaymentMethod transfer = method(2L, "Transfer", false);
        transfer.setStatus(UserStatus.ACTIVE.toString());
        PaymentMethod inactive = method(3L, "Inactive", false);
        inactive.setStatus(UserStatus.INACTIVE.toString());

        when(repository.findByStatusOrderByDefaultMethodDescNameAsc(UserStatus.ACTIVE.toString()))
                .thenReturn(List.of(cash, transfer));

        List<PaymentMethod> result = service.findActive();

        assertEquals(2, result.size());
        assertEquals(cash, result.get(0));
        assertEquals(transfer, result.get(1));
        verify(repository).findByStatusOrderByDefaultMethodDescNameAsc(UserStatus.ACTIVE.toString());
    }

    @Test
    void savePreservesStatusWhenEditing() {
        PaymentMethod existing = method(1L, "Old Name", false);
        existing.setStatus(UserStatus.INACTIVE.toString());

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByNameIgnoreCase("New Name")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        PaymentMethod result = service.save(1L, "New Name", PaymentMethodCategory.CASH);

        assertEquals("New Name", result.getName());
        assertEquals(UserStatus.INACTIVE.toString(), result.getStatus());
    }

    @Test
    void saleCreationAndEditingPersistTheSelectedMethod() {
        var saleService = new SaleService();
        var session = mock(SessionService.class, RETURNS_DEEP_STUBS);
        when(session.getCurrentSession().getUser().getId()).thenReturn(1L);
        org.springframework.test.util.ReflectionTestUtils.setField(saleService, "sessionService", session);
        org.springframework.test.util.ReflectionTestUtils.setField(saleService, "saleRepository", sales);
        org.springframework.test.util.ReflectionTestUtils.setField(saleService, "paymentMethodService", service);
        org.springframework.test.util.ReflectionTestUtils
                .setField(saleService, "configurationService", mock(ConfigurationService.class));
        org.springframework.test.util.ReflectionTestUtils
                .setField(saleService, "productRepository", mock(com.pinodesk.repository.ProductRepository.class));
        org.springframework.test.util.ReflectionTestUtils.setField(
                saleService,
                "receivableRepository",
                mock(com.pinodesk.repository.ReceivableRepository.class));
        org.springframework.test.util.ReflectionTestUtils.setField(
                saleService,
                "saleDetailRepository",
                mock(com.pinodesk.repository.SaleDetailRepository.class));
        PaymentMethod transfer = method(2L, "Transfer BCA", false);
        transfer.setCategory("TRANSFER");
        when(repository.findById(2L)).thenReturn(Optional.of(transfer));
        when(sales.save(any())).thenAnswer(i -> {
            com.pinodesk.entity.Sale sale = i.getArgument(0);
            sale.setId(10L);
            return sale;
        });
        var add = new com.pinodesk.viewmodel.SaleAddVM();
        add.setPaymentMethodId(2L);
        add.setPaymentStatus(com.pinodesk.constant.PaymentStatus.PAID);
        add.setSellingMode(com.pinodesk.constant.SellingMode.GENERAL);
        add.setInvoiceNumber("NEW-1");
        add.setSaleProducts(List.of());
        saleService.createSaleCashier(add);
        var capture = org.mockito.ArgumentCaptor.forClass(com.pinodesk.entity.Sale.class);
        verify(sales).save(capture.capture());
        var saved = capture.getValue();
        assertEquals(2L, saved.getPaymentMethodId());
        when(sales.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(saved));
        var edit = new com.pinodesk.viewmodel.SaleEditVM();
        edit.setPaymentMethodId(1L);
        edit.setPaymentStatus(com.pinodesk.constant.PaymentStatus.PAID);
        edit.setSellingMode(com.pinodesk.constant.SellingMode.GENERAL);
        edit.setInvoiceNumber("NEW-1");
        edit.setSaleProducts(List.of());
        saleService.updateSale(edit, 10L);
        assertEquals(1L, saved.getPaymentMethodId());
        verify(sales, times(2)).save(saved);
    }
}

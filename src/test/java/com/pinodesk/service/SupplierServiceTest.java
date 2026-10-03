package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.Supplier;
import com.pinodesk.entity.SupplierContact;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.SupplierRepository;
import com.pinodesk.repository.SupplierContactRepository;
import com.pinodesk.viewmodel.*;

class SupplierServiceTest extends BaseServiceTest {
    @Test
    void updateCanPopulatePreviouslyNullContacts() {
        Supplier supplier = existing();
        supplier.setEmail(null);
        supplier.setPhone(null);
        SupplierEditVM request = edit();
        service.updateSupplier(request, List.of());
        assertEquals("office@example.test", supplier.getEmail());
        assertEquals("08111", supplier.getPhone());
        verify(suppliers).existsByEmailIgnoreCaseAndDeletedAtIsNull("office@example.test");
        verify(suppliers).existsByPhoneIgnoreCaseAndDeletedAtIsNull("08111");
        verify(suppliers).save(supplier);
    }

    @Mock
    private SupplierRepository suppliers;
    @Mock
    private SupplierContactRepository contacts;
    @InjectMocks
    private SupplierService service;

    private SupplierAddVM addition() {
        SupplierAddVM vm = new SupplierAddVM();
        vm.setCode("S01");
        vm.setName("Supplier");
        vm.setEmail("office@example.test");
        vm.setPhone("08111");
        vm.setWebsite("example.test");
        vm.setAddress("Jakarta");
        return vm;
    }

    private SupplierContactAddVM contact() {
        SupplierContactAddVM vm = new SupplierContactAddVM();
        vm.setName("Sales");
        vm.setEmail("sales@example.test");
        vm.setPhone("08222");
        return vm;
    }

    private Supplier existing() {
        Supplier supplier = new Supplier();
        supplier.setId(7L);
        supplier.setCode("S01");
        supplier.setEmail("office@example.test");
        supplier.setPhone("08111");
        when(suppliers.findByIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(supplier));
        return supplier;
    }

    private SupplierEditVM edit() {
        SupplierEditVM vm = new SupplierEditVM();
        vm.setId(7L);
        vm.setCode("S01");
        vm.setName("Updated");
        vm.setEmail("office@example.test");
        vm.setPhone("08111");
        vm.setAddress("Bandung");
        vm.setWebsite("new.example.test");
        return vm;
    }

    private void saveWithId() {
        when(suppliers.save(any(Supplier.class))).thenAnswer(inv -> {
            Supplier saved = inv.getArgument(0);
            saved.setId(7L);
            return saved;
        });
    }

    @Test
    void createMapsSupplierAndBindsContactToSavedId() {
        saveWithId();
        Supplier saved = service.createSupplier(addition(), List.of(contact()));
        assertEquals("Supplier", saved.getName());
        assertEquals("example.test", saved.getWebsite());
        assertEquals("Jakarta", saved.getAddress());
        ArgumentCaptor<SupplierContact> captor = ArgumentCaptor.forClass(SupplierContact.class);
        verify(contacts).save(captor.capture());
        assertEquals(7L, captor.getValue().getSupplierId());
        assertEquals("Sales", captor.getValue().getName());
        assertEquals("sales@example.test", captor.getValue().getEmail());
        assertEquals("08222", captor.getValue().getPhone());
    }

    @Test
    void createWithoutOptionalContactsSkipsContactRepository() {
        saveWithId();
        SupplierAddVM add = addition();
        add.setEmail(null);
        add.setPhone(" ");
        service.createSupplier(add, null);
        service.createSupplier(add, List.of());
        verifyNoInteractions(contacts);
        verify(suppliers, never()).existsByEmailIgnoreCaseAndDeletedAtIsNull(any());
        verify(suppliers, never()).existsByPhoneIgnoreCaseAndDeletedAtIsNull(any());
    }

    @Test
    void duplicateCodeEmailAndPhonePreventCreation() {
        when(suppliers.existsByCodeIgnoreCaseAndDeletedAtIsNull("S01")).thenReturn(true);
        assertEquals(
                DomainError.SUPPLIER_EXISTS_BY_CODE,
                assertThrows(DomainException.class, () -> service.createSupplier(addition(), List.of())).getError());
        when(suppliers.existsByCodeIgnoreCaseAndDeletedAtIsNull("S01")).thenReturn(false);
        when(suppliers.existsByEmailIgnoreCaseAndDeletedAtIsNull("office@example.test")).thenReturn(true);
        assertEquals(
                DomainError.SUPPLIER_EXISTS_BY_EMAIL,
                assertThrows(DomainException.class, () -> service.createSupplier(addition(), List.of())).getError());
        when(suppliers.existsByEmailIgnoreCaseAndDeletedAtIsNull("office@example.test")).thenReturn(false);
        when(suppliers.existsByPhoneIgnoreCaseAndDeletedAtIsNull("08111")).thenReturn(true);
        assertEquals(
                DomainError.SUPPLIER_EXISTS_BY_PHONE,
                assertThrows(DomainException.class, () -> service.createSupplier(addition(), List.of())).getError());
        verify(suppliers, never()).save(any());
        verifyNoInteractions(contacts);
    }

    @Test
    void duplicateContactEmailOrPhoneRaisesSpecificError() {
        saveWithId();
        when(contacts.existsByEmailIgnoreCaseAndSupplierIdAndDeletedAtIsNull("sales@example.test", 7L))
                .thenReturn(true);
        assertEquals(
                DomainError.SUPPLIER_CONTACT_EXISTS_BY_EMAIL,
                assertThrows(DomainException.class, () -> service.createSupplier(addition(), List.of(contact())))
                        .getError());
        when(contacts.existsByEmailIgnoreCaseAndSupplierIdAndDeletedAtIsNull("sales@example.test", 7L))
                .thenReturn(false);
        when(contacts.existsByPhoneAndSupplierIdAndDeletedAtIsNull("08222", 7L)).thenReturn(true);
        assertEquals(
                DomainError.SUPPLIER_CONTACT_EXISTS_BY_PHONE,
                assertThrows(DomainException.class, () -> service.createSupplier(addition(), List.of(contact())))
                        .getError());
        verify(contacts, never()).save(any());
    }

    @Test
    void contactWithoutEmailOrPhoneCanBeSaved() {
        saveWithId();
        SupplierContactAddVM vm = contact();
        vm.setEmail(null);
        vm.setPhone(" ");
        service.createSupplier(addition(), List.of(vm));
        verify(contacts).save(any(SupplierContact.class));
        verify(contacts, never()).existsByEmailIgnoreCaseAndSupplierIdAndDeletedAtIsNull(any(), any());
        verify(contacts, never()).existsByPhoneAndSupplierIdAndDeletedAtIsNull(any(), any());
    }

    @Test
    void updateUnchangedContactsReplacesContactListAndMapsSupplier() {
        Supplier supplier = existing();
        SupplierEditVM edit = edit();
        edit.setEmail("OFFICE@EXAMPLE.TEST");
        when(suppliers.save(supplier)).thenReturn(supplier);
        assertSame(supplier, service.updateSupplier(edit, List.of(contact())));
        assertEquals("Updated", supplier.getName());
        assertEquals("Bandung", supplier.getAddress());
        assertEquals("new.example.test", supplier.getWebsite());
        org.mockito.InOrder order = inOrder(contacts, suppliers);
        order.verify(contacts).deleteBySupplierId(7L);
        order.verify(contacts).save(any(SupplierContact.class));
        order.verify(suppliers).save(supplier);
        verify(suppliers, never()).existsByCodeIgnoreCaseAndDeletedAtIsNull(any());
        verify(suppliers, never()).existsByEmailIgnoreCaseAndDeletedAtIsNull(any());
        verify(suppliers, never()).existsByPhoneIgnoreCaseAndDeletedAtIsNull(any());
    }

    @Test
    void missingSupplierAndDuplicateChangesPreventUpdate() {
        assertEquals(
                DomainError.SUPPLIER_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.updateSupplier(edit(), null)).getError());
        existing();
        SupplierEditVM edit = edit();
        edit.setCode("S02");
        when(suppliers.existsByCodeIgnoreCaseAndDeletedAtIsNull("S02")).thenReturn(true);
        assertEquals(
                DomainError.SUPPLIER_OTHER_EXISTS_BY_CODE,
                assertThrows(DomainException.class, () -> service.updateSupplier(edit, null)).getError());
        edit.setCode("S01");
        edit.setEmail("new@example.test");
        when(suppliers.existsByEmailIgnoreCaseAndDeletedAtIsNull("new@example.test")).thenReturn(true);
        assertEquals(
                DomainError.SUPPLIER_OTHER_EXISTS_BY_EMAIL,
                assertThrows(DomainException.class, () -> service.updateSupplier(edit, null)).getError());
        edit.setEmail("office@example.test");
        edit.setPhone("08333");
        when(suppliers.existsByPhoneIgnoreCaseAndDeletedAtIsNull("08333")).thenReturn(true);
        assertEquals(
                DomainError.SUPPLIER_OTHER_EXISTS_BY_PHONE,
                assertThrows(DomainException.class, () -> service.updateSupplier(edit, null)).getError());
        verify(suppliers, never()).save(any());
        verifyNoInteractions(contacts);
    }

    @Test
    void updateUniqueContactsOrClearThem() {
        Supplier supplier = existing();
        SupplierEditVM edit = edit();
        edit.setCode("S02");
        edit.setEmail("new@example.test");
        edit.setPhone("08333");
        service.updateSupplier(edit, null);
        assertEquals("S02", supplier.getCode());
        assertEquals("new@example.test", supplier.getEmail());
        assertEquals("08333", supplier.getPhone());
        edit.setEmail(null);
        edit.setPhone("");
        service.updateSupplier(edit, List.of());
        assertNull(supplier.getEmail());
        assertEquals("", supplier.getPhone());
        verify(contacts, times(2)).deleteBySupplierId(7L);
    }

    @Test
    void removalDeletesContactsBeforeSuppliers() {
        service.removeSuppliers(List.of(7L, 8L));
        org.mockito.InOrder order = inOrder(contacts, suppliers);
        order.verify(contacts).deleteUpdateBySupplierIdIn(List.of(7L, 8L));
        order.verify(suppliers).deleteUpdateByIdIn(List.of(7L, 8L));
    }

    @Test
    void searchAndContactLookupMapRequestedData() {
        Supplier supplier = new Supplier();
        supplier.setId(7L);
        supplier.setName("Supplier");
        when(suppliers.findByKeyword("sup")).thenReturn(List.of(supplier));
        assertEquals("Supplier", service.searchSuppliersByKeyword(" sup ").get(0).getName());
        assertTrue(service.searchSuppliersByKeyword(" ").isEmpty());
        verify(suppliers).findByDeletedAtIsNull();
        SupplierFilterVM filter = new SupplierFilterVM();
        when(suppliers.findByFilter(filter)).thenReturn(List.of(supplier));
        assertEquals(7L, service.searchSuppliers(filter).get(0).getId());
        SupplierContact contact = new SupplierContact();
        contact.setSupplierId(7L);
        contact.setName("Sales");
        when(contacts.findBySupplierIdAndDeletedAtIsNull(7L)).thenReturn(List.of(contact));
        assertEquals("Sales", service.getSupplierContacts(7L).get(0).getName());
    }

    @Test
    void supplierCodeStartsAtZeroAndIncrementsLatestSuffix() {
        String first = service.getNextSupplierCode();
        assertTrue(first.matches("[0-9]{8}0000"));
        when(suppliers.findFirstByCodeStartingWithOrderByCodeDesc(anyString())).thenAnswer(inv -> {
            Supplier last = new Supplier();
            last.setCode(inv.<String>getArgument(0) + "0099");
            return Optional.of(last);
        });
        assertTrue(service.getNextSupplierCode().matches("[0-9]{8}0100"));
    }
}

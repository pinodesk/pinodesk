package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.pinodesk.entity.Customer;
import com.pinodesk.repository.CustomerRepository;
import com.pinodesk.viewmodel.*;

class CustomerContactBehaviorTest extends BaseServiceTest {
    @Mock
    private CustomerRepository repository;
    @InjectMocks
    private CustomerService service;

    private CustomerAddVM addition() {
        CustomerAddVM request = new CustomerAddVM();
        request.setName("Alice");
        request.setCode("C01");
        request.setEmail("alice@example.test");
        request.setPhone("081234");
        request.setAddress("Jakarta");
        return request;
    }

    private CustomerEditVM edit() {
        CustomerEditVM request = new CustomerEditVM();
        request.setId(3L);
        request.setCode("C01");
        request.setName("Updated");
        request.setEmail("alice@example.test");
        request.setPhone("081234");
        request.setAddress("Bandung");
        return request;
    }

    private Customer existing() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setCode("C01");
        customer.setName("Alice");
        customer.setEmail("alice@example.test");
        customer.setPhone("081234");
        when(repository.findByIdAndDeletedAtIsNull(3L)).thenReturn(Optional.of(customer));
        return customer;
    }

    @Test
    void createMapsContactDetails() {
        when(repository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        Customer result = service.createCustomer(addition());
        assertEquals("Alice", result.getName());
        assertEquals("C01", result.getCode());
        assertEquals("alice@example.test", result.getEmail());
        assertEquals("081234", result.getPhone());
        assertEquals("Jakarta", result.getAddress());
        verify(repository).save(result);
    }

    @Test
    void unchangedContactsSkipDuplicateChecksAndPreserveIdentity() {
        Customer original = existing();
        CustomerEditVM request = edit();
        request.setEmail("ALICE@EXAMPLE.TEST");
        when(repository.save(original)).thenReturn(original);
        assertSame(original, service.updateCustomer(request));
        assertEquals(3L, original.getId());
        assertEquals("Updated", original.getName());
        assertEquals("Bandung", original.getAddress());
        verify(repository, never()).existsByCodeIgnoreCaseAndDeletedAtIsNull(any());
        verify(repository, never()).existsByEmailIgnoreCaseAndDeletedAtIsNull(any());
        verify(repository, never()).existsByPhoneIgnoreCaseAndDeletedAtIsNull(any());
    }

    @Test
    void changedUniqueContactsArePersisted() {
        Customer original = existing();
        CustomerEditVM request = edit();
        request.setCode("C02");
        request.setEmail("new@example.test");
        request.setPhone("089876");
        service.updateCustomer(request);
        assertEquals("C02", original.getCode());
        assertEquals("new@example.test", original.getEmail());
        assertEquals("089876", original.getPhone());
        verify(repository).save(original);
        verify(repository).existsByCodeIgnoreCaseAndDeletedAtIsNull("C02");
        verify(repository).existsByEmailIgnoreCaseAndDeletedAtIsNull("new@example.test");
        verify(repository).existsByPhoneIgnoreCaseAndDeletedAtIsNull("089876");
    }

    @Test
    void contactsCanBeCleared() {
        Customer original = existing();
        CustomerEditVM request = edit();
        request.setEmail(null);
        request.setPhone("");
        service.updateCustomer(request);
        assertNull(original.getEmail());
        assertEquals("", original.getPhone());
        verify(repository, never()).existsByEmailIgnoreCaseAndDeletedAtIsNull(any());
        verify(repository, never()).existsByPhoneIgnoreCaseAndDeletedAtIsNull(any());
        verify(repository).save(original);
    }

    @Test
    void searchTrimsKeywordAndMapsCustomer() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setName("Alice");
        when(repository.findByKeyword("Alice")).thenReturn(List.of(customer));
        List<CustomerVM> result = service.searchCustomersByKeyword("  Alice  ");
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals("Alice", result.get(0).getName());
        verify(repository).findByKeyword("Alice");
    }

    @Test
    void blankSearchReturnsActiveCustomers() {
        for (String keyword : new String[] { null, "", "   " }) {
            assertTrue(service.searchCustomersByKeyword(keyword).isEmpty());
        }
        verify(repository, times(3)).findByDeletedAtIsNull();
        verify(repository, never()).findByKeyword(any());
    }

    @Test
    void filteredSearchAndRemovalUseRequestedSelection() {
        CustomerFilterVM filter = new CustomerFilterVM();
        Customer customer = new Customer();
        customer.setId(3L);
        when(repository.findByFilter(filter)).thenReturn(List.of(customer));
        assertEquals(3L, service.searchCustomers(filter).get(0).getId());
        service.removeCustomers(List.of(3L, 4L));
        verify(repository).deleteUpdateByIdIn(List.of(3L, 4L));
    }

}

package com.getkembang.kembangdesktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.domain.Customer;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.CustomerRepository;
import com.getkembang.kembangdesktop.viewmodel.CustomerAddVM;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)     
class CustomerServiceTest extends BaseServiceTest {
    
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void testCreateCustomer_allConditionsSatisfied_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setEmail("email");
        customerAdd.setPhone("phone");
        when(customerRepository.existsByCode(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByPhone(anyString())).thenReturn(false);
        when(customerRepository.createCustomer(any(CustomerAddVM.class))).thenReturn(1L);
        Long id = customerService.createCustomer(customerAdd);
        assertEquals(1L, id);
        verify(customerRepository).existsByCode(anyString());
        verify(customerRepository).existsByEmail(anyString());
        verify(customerRepository).existsByPhone(anyString());
        verify(customerRepository).createCustomer(any(CustomerAddVM.class));
    }

    @Test
    void testCreateCustomer_emptyEmail_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setPhone("phone");
        when(customerRepository.existsByCode(anyString())).thenReturn(false);
        when(customerRepository.existsByPhone(anyString())).thenReturn(false);
        when(customerRepository.createCustomer(any(CustomerAddVM.class))).thenReturn(1L);
        Long id = customerService.createCustomer(customerAdd);
        assertEquals(1L, id);
        verify(customerRepository).existsByCode(anyString());
        verify(customerRepository).existsByPhone(anyString());
        verify(customerRepository).createCustomer(any(CustomerAddVM.class));
    }

    @Test
    void testCreateCustomer_emptyPhone_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setEmail("email");
        when(customerRepository.existsByCode(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.createCustomer(any(CustomerAddVM.class))).thenReturn(1L);
        Long id = customerService.createCustomer(customerAdd);
        assertEquals(1L, id);
        verify(customerRepository).existsByCode(anyString());
        verify(customerRepository).existsByEmail(anyString());
        verify(customerRepository).createCustomer(any(CustomerAddVM.class));
    }

    @Test
    void testCreateCustomer_emptyEmailAndPhone_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        when(customerRepository.existsByCode(anyString())).thenReturn(false);
        when(customerRepository.createCustomer(any(CustomerAddVM.class))).thenReturn(1L);
        Long id = customerService.createCustomer(customerAdd);
        assertEquals(1L, id);
        verify(customerRepository).existsByCode(anyString());
        verify(customerRepository).createCustomer(any(CustomerAddVM.class));
    }

    @Test
    void testCreateCustomer_existsByCode_shouldThrowDomainException() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        when(customerRepository.existsByCode(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.createCustomer(customerAdd));
        assertEquals(DomainError.CUSTOMER_EXISTS_BY_CODE, ex.getError());
        verify(customerRepository).existsByCode(anyString());
        verify(customerRepository, never()).createCustomer(any(CustomerAddVM.class));
    }

    @Test
    void testCreateCustomer_existsByEmail_shouldThrowDomainException() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setEmail("email");
        when(customerRepository.existsByCode(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.createCustomer(customerAdd));
        assertEquals(DomainError.CUSTOMER_EXISTS_BY_EMAIL, ex.getError());
        verify(customerRepository).existsByCode(anyString());
        verify(customerRepository, never()).createCustomer(any(CustomerAddVM.class));
    }

    @Test
    void testCreateCustomer_existsByPhone_shouldThrowDomainException() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setEmail("email");
        customerAdd.setPhone("phone");
        when(customerRepository.existsByCode(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByPhone(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.createCustomer(customerAdd));
        assertEquals(DomainError.CUSTOMER_EXISTS_BY_PHONE, ex.getError());
        verify(customerRepository).existsByCode(anyString());
        verify(customerRepository, never()).createCustomer(any(CustomerAddVM.class));
    }

}

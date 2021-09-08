package toscabox.desktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import toscabox.desktop.constant.CommonConstants;
import toscabox.desktop.constant.DomainError;
import toscabox.desktop.domain.Customer;
import toscabox.desktop.exception.DomainException;
import toscabox.desktop.repository.CustomerRepository;
import toscabox.desktop.viewmodel.CustomerAddVM;
import toscabox.desktop.viewmodel.CustomerEditVM;
import toscabox.desktop.viewmodel.CustomerFilterVM;
import toscabox.desktop.viewmodel.CustomerVM;

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
    void testSearchCustomers_shouldSucceed() {
        when(customerRepository.filter(any(CustomerFilterVM.class))).thenReturn(new ArrayList<>());
        List<CustomerVM> customers = customerService.searchCustomers(new CustomerFilterVM());
        assertNotNull(customers);
        assertEquals(0, customers.size());
        verify(customerRepository).filter(any(CustomerFilterVM.class));
    }

    @Test
    void testRemoveCustomers_shouldSucceed() {
        when(customerRepository.delete(anyList())).thenReturn(0);
        customerService.removeCustomers(Collections.singletonList(1L));
        verify(customerRepository).delete(anyList());
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
        assertEquals(1L, id.longValue());
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
        assertEquals(1L, id.longValue());
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
        assertEquals(1L, id.longValue());
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
        assertEquals(1L, id.longValue());
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
        verify(customerRepository).existsByEmail(anyString());
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
        verify(customerRepository).existsByEmail(anyString());
        verify(customerRepository).existsByPhone(anyString());
        verify(customerRepository, never()).createCustomer(any(CustomerAddVM.class));
    }

    @Test
    void testUpdateCustomer_allConditionsSatisfied_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(1L);
        customerEdit.setCode("code");
        customerEdit.setEmail("email");
        customerEdit.setPhone("phone");
        when(customerRepository.exists(anyLong())).thenReturn(true);
        when(customerRepository.existsByCode(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.existsByPhone(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.updateCustomer(any(CustomerEditVM.class))).thenReturn(1);
        boolean success = customerService.updateCustomer(customerEdit);
        assertTrue(success);
        verify(customerRepository).exists(anyLong());
        verify(customerRepository).existsByCode(anyString(), anyLong());
        verify(customerRepository).existsByEmail(anyString(), anyLong());
        verify(customerRepository).existsByPhone(anyString(), anyLong());
        verify(customerRepository).updateCustomer(any(CustomerEditVM.class));
    }

    @Test
    void testUpdateCustomer_emptyEmail_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(1L);
        customerEdit.setCode("code");
        customerEdit.setPhone("phone");
        when(customerRepository.exists(anyLong())).thenReturn(true);
        when(customerRepository.existsByCode(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.existsByPhone(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.updateCustomer(any(CustomerEditVM.class))).thenReturn(1);
        boolean success = customerService.updateCustomer(customerEdit);
        assertTrue(success);
        verify(customerRepository).exists(anyLong());
        verify(customerRepository).existsByCode(anyString(), anyLong());
        verify(customerRepository).existsByPhone(anyString(), anyLong());
        verify(customerRepository).updateCustomer(any(CustomerEditVM.class));
    }

    @Test
    void testUpdateCustomer_emptyPhone_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(1L);
        customerEdit.setCode("code");
        customerEdit.setEmail("email");
        when(customerRepository.exists(anyLong())).thenReturn(true);
        when(customerRepository.existsByCode(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.updateCustomer(any(CustomerEditVM.class))).thenReturn(1);
        boolean success = customerService.updateCustomer(customerEdit);
        assertTrue(success);
        verify(customerRepository).exists(anyLong());
        verify(customerRepository).existsByCode(anyString(), anyLong());
        verify(customerRepository).existsByEmail(anyString(), anyLong());
        verify(customerRepository).updateCustomer(any(CustomerEditVM.class));
    }

    @Test
    void testUpdateCustomer_emptyEmailAndPhone_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(1L);
        customerEdit.setCode("code");
        when(customerRepository.exists(anyLong())).thenReturn(true);
        when(customerRepository.existsByCode(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.updateCustomer(any(CustomerEditVM.class))).thenReturn(1);
        boolean success = customerService.updateCustomer(customerEdit);
        assertTrue(success);
        verify(customerRepository).exists(anyLong());
        verify(customerRepository).existsByCode(anyString(), anyLong());
        verify(customerRepository).updateCustomer(any(CustomerEditVM.class));
    }

    @Test
    void testUpdateCustomer_idNotExists_shouldThrowDomainException() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(2L);
        customerEdit.setCode("code");
        when(customerRepository.exists(anyLong())).thenReturn(false);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.updateCustomer(customerEdit));
        assertEquals(DomainError.CUSTOMER_NOT_FOUND_BY_ID, ex.getError());
        verify(customerRepository).exists(anyLong());
        verify(customerRepository, never()).updateCustomer(any(CustomerEditVM.class));
    }

    @Test
    void testUpdateCustomer_existsByCodeOthers_shouldThrowDomainException() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(2L);
        customerEdit.setCode("code");
        when(customerRepository.exists(anyLong())).thenReturn(true);
        when(customerRepository.existsByCode(anyString(), anyLong())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.updateCustomer(customerEdit));
        assertEquals(DomainError.CUSTOMER_OTHER_EXISTS_BY_CODE, ex.getError());
        verify(customerRepository).exists(anyLong());
        verify(customerRepository).existsByCode(anyString(), anyLong());
        verify(customerRepository, never()).updateCustomer(any(CustomerEditVM.class));
    }

    @Test
    void testUpdateCustomer_existsByEmailOthers_shouldThrowDomainException() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(2L);
        customerEdit.setCode("code");
        customerEdit.setEmail("email");
        when(customerRepository.exists(anyLong())).thenReturn(true);
        when(customerRepository.existsByCode(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString(), anyLong())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.updateCustomer(customerEdit));
        assertEquals(DomainError.CUSTOMER_OTHER_EXISTS_BY_EMAIL, ex.getError());
        verify(customerRepository).exists(anyLong());
        verify(customerRepository).existsByCode(anyString(), anyLong());
        verify(customerRepository).existsByEmail(anyString(), anyLong());
        verify(customerRepository, never()).updateCustomer(any(CustomerEditVM.class));
    }

    @Test
    void testUpdateCustomer_existsByPhoneOthers_shouldThrowDomainException() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(2L);
        customerEdit.setCode("code");
        customerEdit.setEmail("email");
        customerEdit.setPhone("phone");
        when(customerRepository.exists(anyLong())).thenReturn(true);
        when(customerRepository.existsByCode(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.existsByPhone(anyString(), anyLong())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.updateCustomer(customerEdit));
        assertEquals(DomainError.CUSTOMER_OTHER_EXISTS_BY_PHONE, ex.getError());
        verify(customerRepository).exists(anyLong());
        verify(customerRepository).existsByCode(anyString(), anyLong());
        verify(customerRepository).existsByEmail(anyString(), anyLong());
        verify(customerRepository).existsByPhone(anyString(), anyLong());
        verify(customerRepository, never()).updateCustomer(any(CustomerEditVM.class));
    }

    @Test
    void testGetNextCustomerCode_emptyMaxCode_shouldSucceed() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        when(customerRepository.findMaxCodeByPrefix(anyString())).thenReturn(null);
        String nextCode = customerService.getNextCustomerCode();
        assertEquals(prefix + "0000", nextCode);
        verify(customerRepository).findMaxCodeByPrefix(anyString());
    }

    @Test
    void testGetNextCustomerCode_notEmptyMaxCode_shouldSucceed() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        when(customerRepository.findMaxCodeByPrefix(anyString())).thenReturn(prefix + "1000");
        String nextCode = customerService.getNextCustomerCode();
        assertEquals(prefix + "1001", nextCode);
        verify(customerRepository).findMaxCodeByPrefix(anyString());
    }

}

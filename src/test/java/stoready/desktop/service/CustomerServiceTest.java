package stoready.desktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import java.util.Optional;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.DomainError;
import stoready.desktop.domain.Customer;
import stoready.desktop.exception.DomainException;
import stoready.desktop.repository.CustomerRepository;
import stoready.desktop.viewmodel.CustomerAddVM;
import stoready.desktop.viewmodel.CustomerEditVM;
import stoready.desktop.viewmodel.CustomerFilterVM;
import stoready.desktop.viewmodel.CustomerVM;

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
        customer.setCode("abc");
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void testSearchCustomers_shouldSucceed() {
        when(customerRepository.findByFilter(any(CustomerFilterVM.class))).thenReturn(new ArrayList<>());
        List<CustomerVM> customers = customerService.searchCustomers(new CustomerFilterVM());
        assertNotNull(customers);
        assertEquals(0, customers.size());
        verify(customerRepository).findByFilter(any(CustomerFilterVM.class));
    }

    @Test
    void testRemoveCustomers_shouldSucceed() {
        when(customerRepository.deleteUpdateByIdIn(anyList())).thenReturn(0L);
        customerService.removeCustomers(Collections.singletonList(1L));
        verify(customerRepository).deleteUpdateByIdIn(anyList());
    }

    @Test
    void testCreateCustomer_allConditionsSatisfied_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setEmail("email");
        customerAdd.setPhone("phone");
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Customer cust = customerService.createCustomer(customerAdd);
        assertEquals(1L, cust.getId().longValue());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_emptyEmail_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setPhone("phone");
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Customer cust = customerService.createCustomer(customerAdd);
        assertEquals(1L, cust.getId().longValue());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_emptyPhone_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setEmail("email");
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Customer cust = customerService.createCustomer(customerAdd);
        assertEquals(1L, cust.getId().longValue());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_emptyEmailAndPhone_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Customer cust = customerService.createCustomer(customerAdd);
        assertEquals(1L, cust.getId().longValue());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_existsByCode_shouldThrowDomainException() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.createCustomer(customerAdd));
        assertEquals(DomainError.CUSTOMER_EXISTS_BY_CODE, ex.getError());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_existsByEmail_shouldThrowDomainException() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setEmail("email");
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.createCustomer(customerAdd));
        assertEquals(DomainError.CUSTOMER_EXISTS_BY_EMAIL, ex.getError());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_existsByPhone_shouldThrowDomainException() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setCode("code");
        customerAdd.setEmail("email");
        customerAdd.setPhone("phone");
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.createCustomer(customerAdd));
        assertEquals(DomainError.CUSTOMER_EXISTS_BY_PHONE, ex.getError());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_allConditionsSatisfied_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(1L);
        customerEdit.setCode("code");
        customerEdit.setEmail("email");
        customerEdit.setPhone("phone");
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        customerService.updateCustomer(customerEdit);
        verify(customerRepository).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_emptyEmail_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(1L);
        customerEdit.setCode("code");
        customerEdit.setPhone("phone");
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        customerService.updateCustomer(customerEdit);
        verify(customerRepository).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_emptyPhone_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(1L);
        customerEdit.setCode("code");
        customerEdit.setEmail("email");
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        customerService.updateCustomer(customerEdit);
        verify(customerRepository).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_emptyEmailAndPhone_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(1L);
        customerEdit.setCode("code");
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        customerService.updateCustomer(customerEdit);
        verify(customerRepository).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_idNotExists_shouldThrowDomainException() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(2L);
        customerEdit.setCode("code");
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> customerService.updateCustomer(customerEdit));
        assertEquals(DomainError.CUSTOMER_NOT_FOUND_BY_ID, ex.getError());
        verify(customerRepository).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_existsByCodeOthers_shouldThrowDomainException() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(2L);
        customerEdit.setCode("code");
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.updateCustomer(customerEdit));
        assertEquals(DomainError.CUSTOMER_OTHER_EXISTS_BY_CODE, ex.getError());
        verify(customerRepository).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_existsByEmailOthers_shouldThrowDomainException() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(2L);
        customerEdit.setCode("code");
        customerEdit.setEmail("email");
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.updateCustomer(customerEdit));
        assertEquals(DomainError.CUSTOMER_OTHER_EXISTS_BY_EMAIL, ex.getError());
        verify(customerRepository).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_existsByPhoneOthers_shouldThrowDomainException() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setId(2L);
        customerEdit.setCode("code");
        customerEdit.setEmail("email");
        customerEdit.setPhone("phone");
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString())).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> customerService.updateCustomer(customerEdit));
        assertEquals(DomainError.CUSTOMER_OTHER_EXISTS_BY_PHONE, ex.getError());
        verify(customerRepository).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository).existsByCodeIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByEmailIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository).existsByPhoneIgnoreCaseAndDeletedAtIsNull(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testGetNextCustomerCode_emptyMaxCode_shouldSucceed() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        when(customerRepository.findFirstByCodeStartingWithOrderByCodeDesc(anyString())).thenReturn(Optional.empty());
        String nextCode = customerService.getNextCustomerCode();
        assertEquals(prefix + "0000", nextCode);
        verify(customerRepository).findFirstByCodeStartingWithOrderByCodeDesc(anyString());
    }

    @Test
    void testGetNextCustomerCode_notEmptyMaxCode_shouldSucceed() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        customer.setCode(prefix + "1000");
        when(customerRepository.findFirstByCodeStartingWithOrderByCodeDesc(anyString()))
                .thenReturn(Optional.of(customer));
        String nextCode = customerService.getNextCustomerCode();
        assertEquals(prefix + "1001", nextCode);
        verify(customerRepository).findFirstByCodeStartingWithOrderByCodeDesc(anyString());
    }

}

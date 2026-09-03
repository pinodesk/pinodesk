package com.pinodesk.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.constant.Activity;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.Customer;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.CustomerRepository;
import com.pinodesk.viewmodel.CustomerAddVM;
import com.pinodesk.viewmodel.CustomerEditVM;
import com.pinodesk.viewmodel.CustomerFilterVM;
import com.pinodesk.viewmodel.CustomerVM;

@Service
public class CustomerService extends BaseService {

    @Autowired
    private CustomerRepository customerRepository;

    @TargetActivity(Activity.SEARCH_CUSTOMERS_BY_FILTER)
    @Cacheable(CacheNameConstants.CUSTOMERS_BY_FILTER)
    public List<CustomerVM> searchCustomers(CustomerFilterVM filter) {
        return objectConverter.convertList(customerRepository.findByFilter(filter), CustomerVM.class);
    }

    @TargetActivity(Activity.SEARCH_CUSTOMERS_BY_KEYWORD)
    @Cacheable(CacheNameConstants.CUSTOMERS_BY_KEYWORD)
    public List<CustomerVM> searchCustomersByKeyword(String keyword) {
        List<Customer> suppliers = StringUtils.isBlank(keyword) ?
                customerRepository.findByDeletedAtIsNull() : customerRepository.findByKeyword(keyword.trim());
        return objectConverter.convertList(suppliers, CustomerVM.class);
    }

    @TargetActivity(Activity.REMOVE_CUSTOMERS)
    @CacheEvict(value = { CacheNameConstants.CUSTOMERS_BY_FILTER, CacheNameConstants.CUSTOMERS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeCustomers(List<Long> ids) {
        customerRepository.deleteUpdateByIdIn(ids);
    }

    @TargetActivity(Activity.ADD_CUSTOMER)
    @CacheEvict(value = { CacheNameConstants.CUSTOMERS_BY_FILTER, CacheNameConstants.CUSTOMERS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public Customer createCustomer(CustomerAddVM customerAdd) {
        if (customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(customerAdd.getCode())) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_CODE);
        }
        String email = customerAdd.getEmail();
        if (StringUtils.isNotBlank(email) && customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_EMAIL);
        }
        String phone = customerAdd.getPhone();
        if (StringUtils.isNotBlank(phone) && customerRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(phone)) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_PHONE);
        }
        return customerRepository.save(objectConverter.convertObject(customerAdd, Customer.class));
    }

    @TargetActivity(Activity.EDIT_CUSTOMER)
    @CacheEvict(value = { CacheNameConstants.CUSTOMERS_BY_FILTER, CacheNameConstants.CUSTOMERS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public Customer updateCustomer(CustomerEditVM customerEdit) {
        Long customerId = customerEdit.getId();
        String code = customerEdit.getCode();
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() -> new DomainException(DomainError.CUSTOMER_NOT_FOUND_BY_ID));
        if (!customer.getCode().equals(code) && customerRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(code)) {
            throw new DomainException(DomainError.CUSTOMER_OTHER_EXISTS_BY_CODE);
        }
        String email = customerEdit.getEmail();
        if (StringUtils.isNotBlank(email) && !email.equalsIgnoreCase(customer.getEmail())
                && customerRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new DomainException(DomainError.CUSTOMER_OTHER_EXISTS_BY_EMAIL);
        }
        String phone = customerEdit.getPhone();
        if (StringUtils.isNotBlank(phone) && !phone.equals(customer.getPhone())
                && customerRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(phone)) {
            throw new DomainException(DomainError.CUSTOMER_OTHER_EXISTS_BY_PHONE);
        }
        customer.setAddress(customerEdit.getAddress());
        customer.setCode(code);
        customer.setEmail(email);
        customer.setName(customerEdit.getName());
        customer.setPhone(phone);
        return customerRepository.save(customer);
    }

    @TargetActivity(Activity.GET_NEXT_CUSTOMER_CODE)
    public String getNextCustomerCode() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        Optional<Customer> customer = customerRepository.findFirstByCodeStartingWithOrderByCodeDesc(prefix);
        int sequence = 0;
        if (customer.isPresent()) {
            sequence = Integer.parseInt(customer.get().getCode().substring(prefix.length())) + 1;
        }
        return prefix + String.format("%04d", sequence); // Left pad with "0"
    }

}

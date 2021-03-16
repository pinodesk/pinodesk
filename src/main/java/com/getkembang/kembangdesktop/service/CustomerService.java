package com.getkembang.kembangdesktop.service;

import java.util.Date;
import java.util.List;

import com.getkembang.kembangdesktop.constant.CacheName;
import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.CustomerRepository;
import com.getkembang.kembangdesktop.viewmodel.CustomerAddVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerEditVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerFilterVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerVM;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService extends BaseService {

    @Autowired
    private CustomerRepository customerRepository;

    @Cacheable(CacheName.Keys.CUSTOMERS_BY_FILTER)
    public List<CustomerVM> searchContacts(CustomerFilterVM filter) {
        return convertList(customerRepository.filter(filter), CustomerVM.class);
    }

    @CacheEvict(value = CacheName.Keys.CUSTOMERS_BY_FILTER, allEntries = true)
    @Transactional
    public void removeCustomers(List<Long> ids) {
        customerRepository.delete(ids);
    }

    @CacheEvict(value = CacheName.Keys.CUSTOMERS_BY_FILTER, allEntries = true)
    @Transactional
    public Long createCustomer(CustomerAddVM customer) {
        if (customerRepository.existsByCode(customer.getCode())) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_CODE);
        }
        String email = customer.getEmail();
        if (StringUtils.isNotBlank(email) && customerRepository.existsByEmail(email)) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_EMAIL);
        }
        String phone = customer.getPhone();
        if (StringUtils.isNotBlank(phone) && customerRepository.existsByPhone(phone)) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_PHONE);
        }
        return customerRepository.createCustomer(customer);
    }

    @CacheEvict(value = CacheName.Keys.CUSTOMERS_BY_FILTER, allEntries = true)
    @Transactional
    public boolean updateCustomer(CustomerEditVM customer) {
        Long customerId = customer.getId();
        if (!customerRepository.exists(customerId)) {
            throw new DomainException(DomainError.CUSTOMER_NOT_FOUND_BY_ID);
        }
        if (customerRepository.existsByCode(customer.getCode(), customerId)) {
            throw new DomainException(DomainError.CUSTOMER_OTHER_EXISTS_BY_CODE);
        }
        String email = customer.getEmail();
        if (StringUtils.isNotBlank(email) && customerRepository.existsByEmail(email, customerId)) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_EMAIL);
        }
        String phone = customer.getPhone();
        if (StringUtils.isNotBlank(phone) && customerRepository.existsByPhone(phone, customerId)) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_PHONE);
        }
        return customerRepository.updateCustomer(customer) == 1;
    }

    public String getNextCustomerCode() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        String maxCode = customerRepository.findMaxCodeByPrefix(prefix);
        int sequence = 0;
        if (maxCode != null) {
            sequence = Integer.parseInt(maxCode.substring(prefix.length())) + 1;
        }
        return prefix + String.format("%04d", sequence); // Left pad with "0"
    }

}

package com.getkembang.kembangdesktop.service;

import java.util.List;

import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.CustomerRepository;
import com.getkembang.kembangdesktop.viewmodel.CustomerAddVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerEditVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerFilterVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService extends BaseService {

    @Autowired
    private CustomerRepository customerRepository;

    @Cacheable("customersByFilter")
    public List<CustomerVM> searchContacts(CustomerFilterVM filter) {
        return convertList(customerRepository.filter(filter), CustomerVM.class);
    }

    @CacheEvict(value = "customersByFilter", allEntries = true)
    @Transactional
    public void removeCustomers(List<Long> ids) {
        customerRepository.delete(ids);
    }

    @CacheEvict(value = "customersByFilter", allEntries = true)
    @Transactional
    public Long createCustomer(CustomerAddVM customer) {
        if (customerRepository.existsByCode(customer.getCode())) {
            throw new DomainException(DomainError.CUSTOMER_EXISTS_BY_CODE);
        }
        return customerRepository.createCustomer(customer);
    }

    @CacheEvict(value = "customersByFilter", allEntries = true)
    @Transactional
    public boolean updateCustomer(CustomerEditVM customer) {
        if (!customerRepository.exists(customer.getId())) {
            throw new DomainException(DomainError.CUSTOMER_NOT_FOUND_BY_ID);
        }
        if (customerRepository.existsByCode(customer.getCode(), customer.getId())) {
            throw new DomainException(DomainError.CUSTOMER_OTHER_EXISTS_BY_CODE);
        }
        return customerRepository.updateCustomer(customer) == 1;
    }

}

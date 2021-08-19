package toska.desktop.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toska.desktop.constant.CacheName;
import toska.desktop.constant.CommonConstants;
import toska.desktop.constant.DomainError;
import toska.desktop.exception.DomainException;
import toska.desktop.repository.CustomerRepository;
import toska.desktop.viewmodel.CustomerAddVM;
import toska.desktop.viewmodel.CustomerEditVM;
import toska.desktop.viewmodel.CustomerFilterVM;
import toska.desktop.viewmodel.CustomerVM;

@Service
public class CustomerService extends BaseService {

    @Autowired
    private CustomerRepository customerRepository;

    @Cacheable(CacheName.Keys.CUSTOMERS_BY_FILTER)
    public List<CustomerVM> searchCustomers(CustomerFilterVM filter) {
        return objectConverter.convertList(customerRepository.filter(filter), CustomerVM.class);
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
            throw new DomainException(DomainError.CUSTOMER_OTHER_EXISTS_BY_EMAIL);
        }
        String phone = customer.getPhone();
        if (StringUtils.isNotBlank(phone) && customerRepository.existsByPhone(phone, customerId)) {
            throw new DomainException(DomainError.CUSTOMER_OTHER_EXISTS_BY_PHONE);
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

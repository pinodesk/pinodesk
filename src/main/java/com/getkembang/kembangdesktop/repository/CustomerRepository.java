package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Customer;
import com.getkembang.kembangdesktop.viewmodel.CustomerAddVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerEditVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface CustomerRepository extends CommonRepository<Customer> {

	List<Customer> filter(CustomerFilterVM filter);
    
    Long createCustomer(CustomerAddVM customerAdd);

    Integer updateCustomer(CustomerEditVM customerEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByEmail(String email, Long... excludeIds);

    boolean existsByPhone(String phone, Long... excludeIds);

    String findMaxCodeByPrefix(String prefix);
}

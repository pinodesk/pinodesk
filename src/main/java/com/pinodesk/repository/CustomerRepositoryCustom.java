package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.entity.Customer;
import com.pinodesk.viewmodel.CustomerFilterVM;

public interface CustomerRepositoryCustom {

    List<Customer> findByFilter(CustomerFilterVM filter);

    List<Customer> findByKeyword(String keyword);

}

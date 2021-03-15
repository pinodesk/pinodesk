package com.getkembang.kembangdesktop.repository;

import com.getkembang.kembangdesktop.domain.Customer;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryImpl extends AbstractRepository<Customer> implements CustomerRepository {

}

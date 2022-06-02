package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.Customer;
import pinus.desktop.viewmodel.CustomerFilterVM;

public interface CustomerRepositoryCustom {

    List<Customer> findByFilter(CustomerFilterVM filter);

    List<Customer> findByKeyword(String keyword);

}

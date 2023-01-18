package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.domain.Customer;
import pospino.desktop.viewmodel.CustomerFilterVM;

public interface CustomerRepositoryCustom {

    List<Customer> findByFilter(CustomerFilterVM filter);

    List<Customer> findByKeyword(String keyword);

}

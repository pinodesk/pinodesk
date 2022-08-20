package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.domain.Customer;
import stoready.desktop.viewmodel.CustomerFilterVM;

public interface CustomerRepositoryCustom {

    List<Customer> findByFilter(CustomerFilterVM filter);

    List<Customer> findByKeyword(String keyword);

}

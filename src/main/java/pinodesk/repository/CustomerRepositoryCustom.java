package pinodesk.repository;

import java.util.List;

import pinodesk.domain.Customer;
import pinodesk.viewmodel.CustomerFilterVM;

public interface CustomerRepositoryCustom {

    List<Customer> findByFilter(CustomerFilterVM filter);

    List<Customer> findByKeyword(String keyword);

}

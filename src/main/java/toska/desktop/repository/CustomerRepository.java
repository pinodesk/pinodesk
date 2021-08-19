package toska.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toska.desktop.domain.Customer;
import toska.desktop.viewmodel.CustomerAddVM;
import toska.desktop.viewmodel.CustomerEditVM;
import toska.desktop.viewmodel.CustomerFilterVM;

public interface CustomerRepository extends CommonRepository<Customer> {

	List<Customer> filter(CustomerFilterVM filter);
    
    Long createCustomer(CustomerAddVM customerAdd);

    Integer updateCustomer(CustomerEditVM customerEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByEmail(String email, Long... excludeIds);

    boolean existsByPhone(String phone, Long... excludeIds);

    String findMaxCodeByPrefix(String prefix);
}

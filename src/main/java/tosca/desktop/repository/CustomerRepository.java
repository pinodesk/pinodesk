package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.Customer;
import tosca.desktop.viewmodel.CustomerAddVM;
import tosca.desktop.viewmodel.CustomerEditVM;
import tosca.desktop.viewmodel.CustomerFilterVM;
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

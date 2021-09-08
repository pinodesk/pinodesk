package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.Customer;
import toscabox.desktop.viewmodel.CustomerAddVM;
import toscabox.desktop.viewmodel.CustomerEditVM;
import toscabox.desktop.viewmodel.CustomerFilterVM;

public interface CustomerRepository extends CommonRepository<Customer> {

    List<Customer> filter(CustomerFilterVM filter);

    Long createCustomer(CustomerAddVM customerAdd);

    Integer updateCustomer(CustomerEditVM customerEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByEmail(String email, Long... excludeIds);

    boolean existsByPhone(String phone, Long... excludeIds);

    String findMaxCodeByPrefix(String prefix);
}

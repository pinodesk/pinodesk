package toscabox.desktop.repository;

import java.util.Arrays;
import java.util.List;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Order.Direction;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import toscabox.desktop.domain.Customer;
import toscabox.desktop.viewmodel.CustomerAddVM;
import toscabox.desktop.viewmodel.CustomerEditVM;
import toscabox.desktop.viewmodel.CustomerFilterVM;

@Repository
public class CustomerRepositoryImpl extends AbstractRepository<Customer> implements CustomerRepository {

    @Override
    public List<Customer> filter(CustomerFilterVM filter) {
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getName())) {
            where.containsIgnoreCase(Customer.C_NAME, filter.getName());
        }
        if (StringUtils.isNotBlank(filter.getCode())) {
            where.contains(Customer.C_CODE, filter.getCode());
        }
        if (StringUtils.isNotBlank(filter.getPhone())) {
            where.contains(Customer.C_PHONE, filter.getPhone());
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            where.containsIgnoreCase(Customer.C_EMAIL, filter.getEmail());
        }
        if (StringUtils.isNotBlank(filter.getAddress())) {
            where.containsIgnoreCase(Customer.C_ADDRESS, filter.getAddress());
        }
        return read(where);
    }

    @Override
    public Long createCustomer(CustomerAddVM customerAdd) {
        return insert(
                new String[] {
                        Customer.C_NAME,
                        Customer.C_CODE,
                        Customer.C_PHONE,
                        Customer.C_EMAIL,
                        Customer.C_ADDRESS },
                new Object[] {
                        customerAdd.getName(),
                        customerAdd.getCode(),
                        customerAdd.getPhone(),
                        customerAdd.getEmail(),
                        customerAdd.getAddress() });
    }

    @Override
    public Integer updateCustomer(CustomerEditVM customerEdit) {
        return update(
                new String[] {
                        Customer.C_NAME,
                        Customer.C_CODE,
                        Customer.C_PHONE,
                        Customer.C_EMAIL,
                        Customer.C_ADDRESS },
                new Object[] {
                        customerEdit.getName(),
                        customerEdit.getCode(),
                        customerEdit.getPhone(),
                        customerEdit.getEmail(),
                        customerEdit.getAddress() },
                customerEdit.getId());
    }

    @Override
    public boolean existsByCode(String code, Long... excludedIds) {
        Where where = new Where().equalsIgnoreCase(Customer.C_CODE, code);
        if (ArrayUtils.isNotEmpty(excludedIds)) {
            where.andNotIn(DataModel.C_ID, Arrays.asList(excludedIds));
        }
        return exists(where);
    }

    @Override
    public boolean existsByEmail(String email, Long... excludeIds) {
        Where where = new Where().equalsIgnoreCase(Customer.C_EMAIL, email);
        if (ArrayUtils.isNotEmpty(excludeIds)) {
            where.andNotIn(DataModel.C_ID, Arrays.asList(excludeIds));
        }
        return exists(where);
    }

    @Override
    public boolean existsByPhone(String phone, Long... excludeIds) {
        Where where = new Where().equalsIgnoreCase(Customer.C_PHONE, phone);
        if (ArrayUtils.isNotEmpty(excludeIds)) {
            where.andNotIn(DataModel.C_ID, Arrays.asList(excludeIds));
        }
        return exists(where);
    }

    @Override
    public String findMaxCodeByPrefix(String prefix) {
        return readOne(
                new Where().startsWith(Customer.C_CODE, prefix),
                new Order().by(Customer.C_CODE, Direction.DESCENDING),
                true).map(Customer::getCode).orElse(null);
    }

}

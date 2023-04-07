package pospino.desktop.repository;

import java.util.List;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;
import com.gitlab.mudiasoft.sequel.sql.Where;

import pospino.desktop.domain.Customer;
import pospino.desktop.viewmodel.CustomerFilterVM;

import org.apache.commons.lang3.StringUtils;

public class CustomerRepositoryImpl extends AbstractRepository<Customer> implements CustomerRepositoryCustom {

    @Override
    public List<Customer> findByFilter(CustomerFilterVM filter) {
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
    public List<Customer> findByKeyword(String keyword) {
        Where where = new Where().containsIgnoreCase(Customer.C_NAME, keyword)
                .orContainsIgnoreCase(Customer.C_EMAIL, keyword).orContains(Customer.C_CODE, keyword)
                .orContains(Customer.C_PHONE, keyword).orContainsIgnoreCase(Customer.C_ADDRESS, keyword);
        return read(where);
    }

}

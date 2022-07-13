package pinus.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.domain.User;
import pinus.desktop.viewmodel.UserFilterVM;

public class UserRepositoryImpl extends AbstractRepository<User> implements UserRepositoryCustom {

    @Override
    public List<User> findByFilter(UserFilterVM filter) {
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getFullName())) {
            where.containsIgnoreCase(User.C_FULL_NAME, filter.getFullName());
        }
        if (StringUtils.isNotBlank(filter.getUsername())) {
            where.containsIgnoreCase(User.C_USERNAME, filter.getUsername());
        }
        if (filter.getUserGroupId() != null) {
            where.equals(User.C_USER_GROUP_ID, filter.getUserGroupId());
        }
        if (filter.getStatus() != null) {
            where.equals(User.C_STATUS, filter.getStatus().toString());
        }
        return read(where);
    }

}

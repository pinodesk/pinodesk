package pinus.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.domain.User;
import pinus.desktop.viewmodel.UserFilterVM;
import pinus.desktop.viewmodel.UserVM;

public class UserRepositoryImpl extends AbstractRepository<User> implements UserRepositoryCustom {

    @Override
    public List<UserVM> findByFilter(UserFilterVM filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select
                    u.*,
                    ug.name as user_group_name
                from `user` u
                inner join user_group ug on ug.id = u.user_group_id
                """);
        Where where = new Where().isNull("u.deleted_at");
        if (StringUtils.isNotBlank(filter.getFullName())) {
            where.containsIgnoreCase("u.full_name", filter.getFullName());
        }
        if (StringUtils.isNotBlank(filter.getUsername())) {
            where.containsIgnoreCase("u.username", filter.getUsername());
        }
        if (filter.getUserGroup() != null) {
            where.equals("u.user_group_id", filter.getUserGroup().getId());
        }
        if (filter.getStatus() != null) {
            where.equals("u.status", filter.getStatus().toString());
        }
        sb.append(where.getClause());
        return performSelect(sb.toString(), where.getValues(), UserVM.class);
    }

}

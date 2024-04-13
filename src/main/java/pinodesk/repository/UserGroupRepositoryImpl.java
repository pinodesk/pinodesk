package pinodesk.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mudiatech.sequel.repository.AbstractRepository;
import com.mudiatech.sequel.sql.Where;

import pinodesk.domain.UserGroup;
import pinodesk.viewmodel.UserGroupFilterVM;

public class UserGroupRepositoryImpl extends AbstractRepository<UserGroup> implements UserGroupRepositoryCustom {

    @Override
    public List<UserGroup> findByFilter(UserGroupFilterVM filter) {
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getName())) {
            where.containsIgnoreCase(UserGroup.C_NAME, filter.getName());
        }
        if (StringUtils.isNotBlank(filter.getDescription())) {
            where.containsIgnoreCase(UserGroup.C_DESCRIPTION, filter.getDescription());
        }
        if (filter.getStatus() != null) {
            where.equals(UserGroup.C_STATUS, filter.getStatus().toString());
        }
        return read(where);
    }

    @Override
    public List<UserGroup> findByKeyword(String keyword) {
        Where where = new Where().containsIgnoreCase(UserGroup.C_NAME, keyword)
                .orContainsIgnoreCase(UserGroup.C_DESCRIPTION, keyword)
                .orContainsIgnoreCase(UserGroup.C_STATUS, keyword);
        return read(where);
    }

}

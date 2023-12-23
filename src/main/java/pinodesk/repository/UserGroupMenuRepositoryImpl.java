package pinodesk.repository;

import java.util.List;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;

import pinodesk.domain.UserGroupMenu;
import pinodesk.viewmodel.UserGroupMenuVM;

public class UserGroupMenuRepositoryImpl extends AbstractRepository<UserGroupMenu>
        implements UserGroupMenuRepositoryCustom {

    @Override
    public List<UserGroupMenuVM> findByUserGroupId(Long userGroupId, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select
                    b.id,
                    b.user_group_id,
                    a.id as menu_id,
                    a.parent_menu_id,
                    a.name as menu_name,
                    a.code as menu_code,
                    a.language,
                    coalesce(b.read, 'no') as read,
                    coalesce(b.write, 'no') as write
                from menu a
                left join user_group_menu b on b.menu_code = a.code
                where a.language = ? and b.user_group_id = ?
                order by a.parent_menu_id, a.id
                """);
        return performSelect(sb.toString(), List.of(language, userGroupId), UserGroupMenuVM.class);
    }

}

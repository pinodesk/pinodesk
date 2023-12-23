package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.UserFilterVM;
import pinodesk.viewmodel.UserVM;

public interface UserRepositoryCustom {

    List<UserVM> findByFilter(UserFilterVM filter);
}

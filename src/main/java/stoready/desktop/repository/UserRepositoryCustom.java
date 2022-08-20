package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.UserFilterVM;
import stoready.desktop.viewmodel.UserVM;

public interface UserRepositoryCustom {

    List<UserVM> findByFilter(UserFilterVM filter);
}

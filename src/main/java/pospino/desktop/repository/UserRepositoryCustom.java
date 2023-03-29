package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.UserFilterVM;
import pospino.desktop.viewmodel.UserVM;

public interface UserRepositoryCustom {

    List<UserVM> findByFilter(UserFilterVM filter);
}

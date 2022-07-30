package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.UserFilterVM;
import pinus.desktop.viewmodel.UserVM;

public interface UserRepositoryCustom {

    List<UserVM> findByFilter(UserFilterVM filter);
}

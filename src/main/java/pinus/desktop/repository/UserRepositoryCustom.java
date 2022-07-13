package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.User;
import pinus.desktop.viewmodel.UserFilterVM;

public interface UserRepositoryCustom {

    List<User> findByFilter(UserFilterVM filter);
}

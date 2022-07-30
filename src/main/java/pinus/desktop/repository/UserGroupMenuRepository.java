package pinus.desktop.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.UserGroupMenu;

@Repository
public interface UserGroupMenuRepository
        extends PagingAndSortingRepository<UserGroupMenu, Long>, UserGroupMenuRepositoryCustom {

}

package pinus.desktop.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.UserGroup;

@Repository
public interface UserGroupRepository extends PagingAndSortingRepository<UserGroup, Long>, UserGroupRepositoryCustom {

}

package pinus.desktop.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Login;

@Repository
public interface LoginRepository extends PagingAndSortingRepository<Login, Long> {

}

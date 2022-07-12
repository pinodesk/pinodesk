package pinus.desktop.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Menu;

@Repository
public interface MenuRepository extends PagingAndSortingRepository<Menu, Long> {

}

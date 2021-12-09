package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.DrugCategory;

public interface DrugCategoryRepository extends CommonRepository<DrugCategory> {

    List<DrugCategory> filter(String keyword, Long drugCategoryBaseId);

}

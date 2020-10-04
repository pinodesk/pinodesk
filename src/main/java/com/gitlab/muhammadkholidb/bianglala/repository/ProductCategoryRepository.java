package com.gitlab.muhammadkholidb.bianglala.repository;

import com.gitlab.muhammadkholidb.bianglala.entity.LanguageEntity;
import com.gitlab.muhammadkholidb.bianglala.entity.ProductCategoryEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {
    
    @Query("SELECT pc FROM ProductCategoryEntity pc WHERE pc.language = ?2 AND LOWER(pc.name) LIKE %?1% ")
    List<ProductCategoryEntity> findByKeyword(String keyword, LanguageEntity language, Pageable pageable);
    
}

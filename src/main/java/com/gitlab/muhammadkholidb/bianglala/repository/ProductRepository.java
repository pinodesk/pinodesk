package com.gitlab.muhammadkholidb.bianglala.repository;


import com.gitlab.muhammadkholidb.bianglala.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, ProductRepositoryCustom {
    
}

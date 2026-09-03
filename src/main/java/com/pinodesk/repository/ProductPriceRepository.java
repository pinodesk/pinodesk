package com.pinodesk.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pinodesk.entity.ProductPrice;
import com.pinodesk.viewmodel.ProductPriceVM;

@Repository
public interface ProductPriceRepository extends PagingAndSortingRepository<ProductPrice, Long> {

    @Query("""
            select
            a.*,
            b.full_name as user_full_name,
            b.username as user_username
            from product_price a
            inner join `user` b on b.id = a.user_id
            where a.product_id = :productId and a.deleted_at is null
            order by a.id desc
            """)
    List<ProductPriceVM> findByProductIdOrderByIdDesc(@Param("productId") Long productId);

    List<ProductPrice> findFirst2ByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);
}

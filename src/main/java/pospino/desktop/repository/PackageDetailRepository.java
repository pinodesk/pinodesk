package pospino.desktop.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pospino.desktop.domain.PackageDetail;
import pospino.desktop.viewmodel.PackageProductVM;

import java.util.List;

@Repository
public interface PackageDetailRepository extends PagingAndSortingRepository<PackageDetail, Long> {

    @Query("""
            select b.*,
                a.quantity as quantity_in_package,
                c.id as unit_id,
                c.label as unit_label
            from package_detail a
            inner join product b on b.id = a.package_product_id
            inner join unit c on c.code = b.unit_code and c.language = :language
            where a.product_id = :productId and a.deleted_at is null and b.deleted_at is null
            """)
    public List<PackageProductVM> findByProductId(
            @Param("productId") Long productId,
            @Param("language") String language);

    @Transactional
    @Modifying
    @Query("delete from package_detail where product_id = :productId")
    Long deleteByProductId(@Param("productId") Long productId);

}

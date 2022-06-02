package pinus.desktop.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.domain.SaleDetail;

@Repository
public interface SaleDetailRepository extends PagingAndSortingRepository<SaleDetail, Long> {

    @Transactional
    @Modifying
    @Query("update sale_detail set updated_at=now(), deleted_at=now() where sale_id in (:saleIds)")
    Long deleteUpdateBySaleIdIn(@Param("saleIds") List<Long> saleIds);

}

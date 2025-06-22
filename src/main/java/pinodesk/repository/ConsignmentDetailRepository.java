package pinodesk.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinodesk.entity.ConsignmentDetail;

@Repository
public interface ConsignmentDetailRepository extends PagingAndSortingRepository<ConsignmentDetail, Long> {
}

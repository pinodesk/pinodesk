package pinodesk.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinodesk.entity.Consignment;

@Repository
public interface ConsignmentRepository
        extends PagingAndSortingRepository<Consignment, Long>, ConsignmentRepositoryCustom {

}

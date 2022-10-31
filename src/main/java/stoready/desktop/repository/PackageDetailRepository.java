package stoready.desktop.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import stoready.desktop.domain.PackageDetail;

@Repository
public interface PackageDetailRepository extends PagingAndSortingRepository<PackageDetail, Long> {

}

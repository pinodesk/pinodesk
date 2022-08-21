package stoready.desktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import stoready.desktop.domain.Configuration;

@Repository
public interface ConfigurationRepository extends PagingAndSortingRepository<Configuration, Long> {

    List<Configuration> findByDeletedAtIsNull();

    Optional<Configuration> findByCodeAndDeletedAtIsNull(String code);

    @Transactional
    @Modifying
    @Query("update configuration set `value`=:val, updated_at=now() where code=:code")
    Integer updateValueByCode(@Param("code") String code, @Param("val") String value);

}

package pinus.desktop.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.domain.Session;

@Repository
public interface SessionRepository extends PagingAndSortingRepository<Session, Long> {

    Optional<Session> findFirstByDeletedAtIsNullOrderByIdDesc();

    @Transactional
    @Modifying
    @Query("update `session` set updated_at=now(), deleted_at=now() where deleted_at is null")
    Integer deleteUpdateByDeletedAtIsNull();

}

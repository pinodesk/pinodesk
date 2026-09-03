package com.pinodesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.entity.Customer;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, CustomerRepositoryCustom {

    List<Customer> findByDeletedAtIsNull();

    Optional<Customer> findByIdAndDeletedAtIsNull(long id);

    boolean existsByIdAndDeletedAtIsNull(long id);

    boolean existsByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    boolean existsByPhoneIgnoreCaseAndDeletedAtIsNull(String phone);

    boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);

    Optional<Customer> findFirstByCodeStartingWithOrderByCodeDesc(String prefix);

    @Transactional
    @Modifying
    @Query("update customer set updated_at=now(), deleted_at=now() where id in (:ids)")
    Long deleteUpdateByIdIn(@Param("ids") List<Long> ids);

}

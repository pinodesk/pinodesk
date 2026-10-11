package com.pinodesk.repository;

import com.pinodesk.entity.Expense;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Long>, ExpenseRepositoryCustom {

    @Transactional
    @Modifying
    @Query("update expense set updated_at=now(), deleted_at=now() where id in (:ids)")
    void deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    boolean existsByExpenseCategoryIdAndDeletedAtIsNull(Long expenseCategoryId);
}

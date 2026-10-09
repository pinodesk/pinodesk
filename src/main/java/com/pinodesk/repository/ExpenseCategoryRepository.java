package com.pinodesk.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.pinodesk.entity.ExpenseCategory;

public interface ExpenseCategoryRepository extends CrudRepository<ExpenseCategory, Long> {
    List<ExpenseCategory> findAllByOrderByNameAsc();

    Optional<ExpenseCategory> findByNameIgnoreCase(String name);
}

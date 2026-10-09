package com.pinodesk.service;

import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.ExpenseCategory;
import com.pinodesk.repository.ExpenseCategoryRepository;

@Service
public class ExpenseCategoryService {
    private final ExpenseCategoryRepository repository;

    public ExpenseCategoryService(ExpenseCategoryRepository repository) {
        this.repository = repository;
    }

    public List<ExpenseCategory> findAll() {
        return repository.findAllByOrderByNameAsc();
    }

    @Transactional
    public ExpenseCategory save(Long id, String name, UserStatus status) {
        if (name == null || name.isBlank() || name.trim().length() > 100 || status == null) {
            throw new IllegalArgumentException("Nama kategori dan status wajib diisi.");
        }
        String cleanedName = name.trim();
        repository.findByNameIgnoreCase(cleanedName).ifPresent(existing -> {
            if (!Objects.equals(existing.getId(), id)) {
                throw new IllegalArgumentException("Nama kategori pengeluaran sudah digunakan.");
            }
        });
        ExpenseCategory category = id == null ?
                new ExpenseCategory() : repository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Kategori pengeluaran tidak ditemukan."));
        category.setName(cleanedName);
        category.setStatus(status.toString());
        try {
            return repository.save(category);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("Nama kategori pengeluaran sudah digunakan.", exception);
        }
    }

    @Transactional
    public void remove(List<Long> ids) {
        List<ExpenseCategory> categories = ids.stream().distinct().map(
                id -> repository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Kategori pengeluaran tidak ditemukan.")))
                .toList();
        // TODO: Check whether each category is referenced by an expense transaction
        // before allowing deletion.
        repository.deleteAll(categories);
    }
}

package com.pinodesk.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.pinodesk.entity.PaymentMethod;

public interface PaymentMethodRepository extends CrudRepository<PaymentMethod, Long> {
    List<PaymentMethod> findAllByOrderByDefaultMethodDescNameAsc();

    List<PaymentMethod> findByStatusOrderByDefaultMethodDescNameAsc(String status);

    Optional<PaymentMethod> findByDefaultMethodTrue();

    Optional<PaymentMethod> findByNameIgnoreCase(String name);
}

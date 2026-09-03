package com.pinodesk.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pinodesk.entity.Consignment;

@Repository
public interface ConsignmentRepository
        extends PagingAndSortingRepository<Consignment, Long>, ConsignmentRepositoryCustom {

    boolean existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull(String invoiceNumber, Long supplierId);

}

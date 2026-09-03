package com.pinodesk.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pinodesk.entity.ConsignmentDetail;

@Repository
public interface ConsignmentDetailRepository extends PagingAndSortingRepository<ConsignmentDetail, Long> {
}

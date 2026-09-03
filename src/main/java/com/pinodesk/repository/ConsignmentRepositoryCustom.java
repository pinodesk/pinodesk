package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.ConsignmentFilterVM;
import com.pinodesk.viewmodel.ConsignmentVM;

public interface ConsignmentRepositoryCustom {
    List<ConsignmentVM> findByFilter(ConsignmentFilterVM filter);
}
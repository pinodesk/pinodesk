package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.SaleFilterVM;
import com.pinodesk.viewmodel.SaleVM;

public interface SaleRepositoryCustom {

    List<SaleVM> findByFilter(SaleFilterVM filter);

}

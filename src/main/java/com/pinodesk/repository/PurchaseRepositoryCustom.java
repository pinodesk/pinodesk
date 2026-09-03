package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.PurchaseFilterVM;
import com.pinodesk.viewmodel.PurchaseVM;

public interface PurchaseRepositoryCustom {

    List<PurchaseVM> findByFilter(PurchaseFilterVM filter);

}

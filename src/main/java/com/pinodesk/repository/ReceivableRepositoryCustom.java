package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.ReceivableFilterVM;
import com.pinodesk.viewmodel.ReceivableVM;

public interface ReceivableRepositoryCustom {

    List<ReceivableVM> findByFilter(ReceivableFilterVM filter);

}

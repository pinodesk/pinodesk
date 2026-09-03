package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.PayableFilterVM;
import com.pinodesk.viewmodel.PayableVM;

public interface PayableRepositoryCustom {

    List<PayableVM> findByFilter(PayableFilterVM filter);

}

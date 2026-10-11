package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.ExpenseFilterVM;
import com.pinodesk.viewmodel.ExpenseVM;

public interface ExpenseRepositoryCustom {

    List<ExpenseVM> findByFilter(ExpenseFilterVM filter);

}

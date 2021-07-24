package com.getkembang.kembangdesktop.service;

import com.getkembang.kembangdesktop.domain.Drug;
import com.getkembang.kembangdesktop.repository.DrugRepository;
import com.getkembang.kembangdesktop.viewmodel.DrugVM;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrugService extends BaseService {

    @Autowired
    private DrugRepository drugRepository;

    public DrugVM getDrugByProductId(Long productId) {
        return objectConverter.convertOptional(drugRepository.readOne(new Where().equals(Drug.C_PRODUCT_ID, productId)),
                DrugVM.class);
    }

}

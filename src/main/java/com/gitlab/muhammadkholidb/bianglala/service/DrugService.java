package com.gitlab.muhammadkholidb.bianglala.service;

import com.gitlab.muhammadkholidb.bianglala.domain.Drug;
import com.gitlab.muhammadkholidb.bianglala.repository.DrugRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugVM;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrugService extends BaseService {

    @Autowired
    private DrugRepository drugRepository;

    public DrugVM getDrugByProductId(Long productId) {
        return convertOptional(drugRepository.readOne(new Where().equals(Drug.C_PRODUCT_ID, productId)), DrugVM.class);
    }

}

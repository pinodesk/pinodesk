package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.Wholesale;
import com.gitlab.muhammadkholidb.bianglala.repository.WholesaleRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.WholesaleVM;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Order;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WholesaleService extends BaseService {

    @Autowired
    private WholesaleRepository wholesaleRepository;

    public List<WholesaleVM> getWholesalesByProductId(Long productId) {
        return convertList(wholesaleRepository.read(new Where().equals(Wholesale.C_PRODUCT_ID, productId),
                new Order().by(Wholesale.C_PURCHASE_QUANTITY)), WholesaleVM.class);
    }

}

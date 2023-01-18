package pospino.desktop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pospino.desktop.annotation.ForActivity;
import pospino.desktop.constant.Activity;
import pospino.desktop.repository.DrugRepository;
import pospino.desktop.viewmodel.DrugVM;

@Service
public class DrugService extends BaseService {

    @Autowired
    private DrugRepository drugRepository;

    @ForActivity(Activity.GET_DRUG_BY_PRODUCT_ID)
    public DrugVM getDrugByProductId(Long productId) {
        return objectConverter
                .convertOptional(drugRepository.findByProductIdAndDeletedAtIsNull(productId), DrugVM.class);
    }

}

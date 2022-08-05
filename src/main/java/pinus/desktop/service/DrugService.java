package pinus.desktop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pinus.desktop.annotation.ForActivity;
import pinus.desktop.constant.Activity;
import pinus.desktop.repository.DrugRepository;
import pinus.desktop.viewmodel.DrugVM;

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

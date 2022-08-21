package stoready.desktop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stoready.desktop.annotation.ForActivity;
import stoready.desktop.constant.Activity;
import stoready.desktop.repository.DrugRepository;
import stoready.desktop.viewmodel.DrugVM;

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

package pinodesk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pinodesk.annotation.ForActivity;
import pinodesk.constant.Activity;
import pinodesk.repository.DrugRepository;
import pinodesk.viewmodel.DrugVM;

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

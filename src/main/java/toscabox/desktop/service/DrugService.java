package toscabox.desktop.service;

import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import toscabox.desktop.domain.Drug;
import toscabox.desktop.repository.DrugRepository;
import toscabox.desktop.viewmodel.DrugVM;

@Service
public class DrugService extends BaseService {

    @Autowired
    private DrugRepository drugRepository;

    public DrugVM getDrugByProductId(Long productId) {
        return objectConverter.convertOptional(drugRepository.readOne(new Where().equals(Drug.C_PRODUCT_ID, productId)),
                DrugVM.class);
    }

}

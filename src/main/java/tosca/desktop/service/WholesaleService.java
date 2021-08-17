package tosca.desktop.service;

import java.util.List;

import tosca.desktop.domain.Wholesale;
import tosca.desktop.repository.WholesaleRepository;
import tosca.desktop.viewmodel.WholesaleVM;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WholesaleService extends BaseService {

    @Autowired
    private WholesaleRepository wholesaleRepository;

    public List<WholesaleVM> getWholesalesByProductId(Long productId) {
        return objectConverter
                .convertList(wholesaleRepository.read(new Where().equals(Wholesale.C_PRODUCT_ID, productId),
                        new Order().by(Wholesale.C_PURCHASE_QUANTITY)), WholesaleVM.class);
    }

}

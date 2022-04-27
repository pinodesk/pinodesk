package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Sale;
import pinus.desktop.viewmodel.SaleFilterVM;
import pinus.desktop.viewmodel.SaleVM;

@Repository
public class SaleRepositoryImpl extends AbstractRepository<Sale> implements SaleRepository {

    @Override
    public List<SaleVM> filter(SaleFilterVM filter) {
        return performSelect("select * from sale", null, SaleVM.class);
    }

}

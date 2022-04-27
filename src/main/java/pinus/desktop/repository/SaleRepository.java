package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.Sale;
import pinus.desktop.viewmodel.SaleFilterVM;
import pinus.desktop.viewmodel.SaleVM;

public interface SaleRepository extends CommonRepository<Sale> {

    List<SaleVM> filter(SaleFilterVM filter);

}

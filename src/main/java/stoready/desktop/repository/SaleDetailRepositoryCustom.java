package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.SaleProductVM;

public interface SaleDetailRepositoryCustom {

    List<SaleProductVM> findBySaleIdJoinProducts(Long saleId, String language);
}

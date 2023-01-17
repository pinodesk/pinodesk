package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.SaleProductVM;

public interface SaleDetailRepositoryCustom {

    List<SaleProductVM> findBySaleIdJoinProducts(Long saleId, String language);
}

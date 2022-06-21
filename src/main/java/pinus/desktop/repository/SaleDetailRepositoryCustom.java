package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.SaleProductVM;

public interface SaleDetailRepositoryCustom {

    List<SaleProductVM> findBySaleIdJoinProducts(Long saleId, String languageCode);
}

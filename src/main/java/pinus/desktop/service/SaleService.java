package pinus.desktop.service;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.domain.SaleDetail;
import pinus.desktop.repository.SaleDetailRepository;
import pinus.desktop.repository.SaleRepository;
import pinus.desktop.viewmodel.SaleFilterVM;
import pinus.desktop.viewmodel.SaleVM;

@Service
public class SaleService extends BaseService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Cacheable(CacheNameConstants.SALES_BY_FILTER)
    public List<SaleVM> searchSales(SaleFilterVM filter) {
        return saleRepository.filter(filter);
    }

    @CacheEvict(value = { CacheNameConstants.SALES_BY_FILTER }, allEntries = true)
    @Transactional
    public void removeSales(List<Long> ids) {
        saleDetailRepository.delete(new Where().in(SaleDetail.C_SALE_ID, ids));
        saleRepository.delete(ids);
    }

}

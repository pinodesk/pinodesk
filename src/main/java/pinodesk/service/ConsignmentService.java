package pinodesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.annotation.TargetActivity;
import pinodesk.constant.Activity;
import pinodesk.constant.CacheNameConstants;
import pinodesk.repository.ConsignmentRepository;
import pinodesk.viewmodel.ConsignmentFilterVM;
import pinodesk.viewmodel.ConsignmentVM;

@Service
public class ConsignmentService extends BaseService {

    @Autowired
    private ConsignmentRepository consignmentRepository;

    @TargetActivity(Activity.SEARCH_CONSIGNMENTS_BY_FILTER)
    @Cacheable(CacheNameConstants.CONSIGNMENTS_BY_FILTER)
    public List<ConsignmentVM> searchConsignments(ConsignmentFilterVM filter) {
        return objectConverter.convertList(consignmentRepository.findByFilter(filter), ConsignmentVM.class);
    }

    @TargetActivity(Activity.REMOVE_CONSIGNMENTS)
    @CacheEvict(value = {
            CacheNameConstants.CONSIGNMENTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PAYABLES_BY_FILTER },
        allEntries = true)
    @Transactional
    public void removeConsignments(List<Long> consignmentIds) {

    }

}
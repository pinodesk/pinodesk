package pinus.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.repository.PayableRepository;
import pinus.desktop.viewmodel.PayableVM;

@Service
public class PayableService extends BaseService {

    @Autowired
    private PayableRepository payableRepository;

    @Cacheable(CacheNameConstants.PAYABLES_BY_FILTER)
    public List<PayableVM> searchPayables() {
        return objectConverter.convertList(Streamable.of(payableRepository.findAll()).toList(), PayableVM.class);
    }

}

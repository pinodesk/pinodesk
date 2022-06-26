package pinus.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.repository.ReceivableRepository;
import pinus.desktop.viewmodel.ReceivableVM;

@Service
public class ReceivableService extends BaseService {

    @Autowired
    private ReceivableRepository receivableRepository;

    @Cacheable(CacheNameConstants.PAYABLES_BY_FILTER)
    public List<ReceivableVM> searchReceivables() {
        return objectConverter.convertList(Streamable.of(receivableRepository.findAll()).toList(), ReceivableVM.class);
    }

}

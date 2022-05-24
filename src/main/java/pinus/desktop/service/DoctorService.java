package pinus.desktop.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.domain.Doctor;
import pinus.desktop.repository.DoctorRepository;
import pinus.desktop.viewmodel.DoctorVM;

@Service
public class DoctorService extends BaseService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Cacheable(CacheNameConstants.DOCTORS_BY_KEYWORD)
    public List<DoctorVM> searchDoctorsByKeyword(String keyword) {
        List<Doctor> doctors = StringUtils.isBlank(keyword) ?
                doctorRepository.findByDeletedAtIsNull() : doctorRepository.findByKeyword(keyword.trim());
        return objectConverter.convertList(doctors, DoctorVM.class);
    }

}

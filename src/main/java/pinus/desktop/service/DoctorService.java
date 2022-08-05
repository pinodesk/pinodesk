package pinus.desktop.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.annotation.ForActivity;
import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.Doctor;
import pinus.desktop.domain.DoctorCategory;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.DoctorCategoryRepository;
import pinus.desktop.repository.DoctorRepository;
import pinus.desktop.viewmodel.DoctorAddVM;
import pinus.desktop.viewmodel.DoctorCategoryVM;
import pinus.desktop.viewmodel.DoctorVM;

@Service
public class DoctorService extends BaseService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorCategoryRepository doctorCategoryRepository;

    @Autowired
    private ConfigurationService configurationService;

    @ForActivity(Activity.SEARCH_DOCTORS_BY_KEYWORD)
    @Cacheable(CacheNameConstants.DOCTORS_BY_KEYWORD)
    public List<DoctorVM> searchDoctorsByKeyword(String keyword) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return doctorRepository.findByKeyword(keyword.trim(), language);
    }

    @ForActivity(Activity.ADD_DOCTOR)
    @CacheEvict(value = { CacheNameConstants.DOCTORS_BY_FILTER, CacheNameConstants.DOCTORS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public Doctor createDoctor(DoctorAddVM doctorAdd) {
        if (doctorRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(doctorAdd.getCode())) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_CODE);
        }
        String registrationNumber = doctorAdd.getRegistrationNumber();
        if (StringUtils.isNotBlank(registrationNumber)
                && doctorRepository.existsByRegistrationNumberAndDeletedAtIsNull(registrationNumber)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_REGISTRATION_NUMBER);
        }
        String medicalLicenseNumber = doctorAdd.getMedicalLicenseNumber();
        if (StringUtils.isNotBlank(medicalLicenseNumber)
                && doctorRepository.existsByMedicalLicenseNumberAndDeletedAtIsNull(medicalLicenseNumber)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_MEDICAL_LICENSE_NUMBER);
        }
        return doctorRepository.save(objectConverter.convertObject(doctorAdd, Doctor.class));
    }

    public String getNextDoctorCode() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        Optional<Doctor> doctor = doctorRepository.findFirstByCodeStartingWithOrderByCodeDesc(prefix);
        int sequence = 0;
        if (doctor.isPresent()) {
            sequence = Integer.parseInt(doctor.get().getCode().substring(prefix.length())) + 1;
        }
        return prefix + String.format("%04d", sequence); // Left pad with "0"
    }

    @ForActivity(Activity.SEARCH_DOCTOR_CATEGORIES_BY_KEYWORD)
    @Cacheable(CacheNameConstants.DOCTOR_CATEGORIES_BY_KEYWORD)
    public List<DoctorCategoryVM> searchDoctorCategoryByKeyword(String keyword) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        List<DoctorCategory> categories = doctorCategoryRepository.findByKeyword(keyword, language);
        return objectConverter.convertList(categories, DoctorCategoryVM.class);
    }

}

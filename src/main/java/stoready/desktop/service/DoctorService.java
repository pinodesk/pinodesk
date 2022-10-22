package stoready.desktop.service;

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

import stoready.desktop.annotation.ForActivity;
import stoready.desktop.constant.Activity;
import stoready.desktop.constant.CacheNameConstants;
import stoready.desktop.constant.CommonConstants;
import stoready.desktop.constant.ConfigurationConstants;
import stoready.desktop.constant.DomainError;
import stoready.desktop.domain.Doctor;
import stoready.desktop.domain.DoctorCategory;
import stoready.desktop.exception.DomainException;
import stoready.desktop.repository.DoctorCategoryRepository;
import stoready.desktop.repository.DoctorRepository;
import stoready.desktop.viewmodel.DoctorAddVM;
import stoready.desktop.viewmodel.DoctorCategoryVM;
import stoready.desktop.viewmodel.DoctorEditVM;
import stoready.desktop.viewmodel.DoctorFilterVM;
import stoready.desktop.viewmodel.DoctorVM;

@Service
public class DoctorService extends BaseService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorCategoryRepository doctorCategoryRepository;

    @Autowired
    private ConfigurationService configurationService;

    @ForActivity(Activity.SEARCH_DOCTORS_BY_FILTER)
    @Cacheable(CacheNameConstants.DOCTORS_BY_FILTER)
    public List<DoctorVM> searchDoctorsByFilter(DoctorFilterVM filter) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return doctorRepository.findByFilter(filter, language);
    }

    @ForActivity(Activity.REMOVE_DOCTORS)
    @CacheEvict(value = { CacheNameConstants.DOCTORS_BY_FILTER, CacheNameConstants.DOCTORS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeDoctors(List<Long> ids) {
        doctorRepository.deleteUpdateByIdIn(ids);
    }

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
        String email = doctorAdd.getEmail();
        if (StringUtils.isNotBlank(email) && doctorRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_EMAIL);
        }
        String phone = doctorAdd.getPhone();
        if (StringUtils.isNotBlank(phone) && doctorRepository.existsByPhoneAndDeletedAtIsNull(phone)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_PHONE);
        }
        return doctorRepository.save(objectConverter.convertObject(doctorAdd, Doctor.class));
    }

    @ForActivity(Activity.EDIT_DOCTOR)
    @CacheEvict(value = { CacheNameConstants.DOCTORS_BY_FILTER, CacheNameConstants.DOCTORS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public Doctor updateDoctor(DoctorEditVM doctorEdit, Long doctorId) {
        String code = doctorEdit.getCode();
        Doctor doctor = doctorRepository.findByIdAndDeletedAtIsNull(doctorId)
                .orElseThrow(() -> new DomainException(DomainError.DOCTOR_NOT_FOUND_BY_ID));
        if (!doctor.getCode().equals(code) && doctorRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(code)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_CODE);
        }
        String registrationNumber = doctorEdit.getRegistrationNumber();
        if (StringUtils.isNotBlank(registrationNumber) && !doctor.getRegistrationNumber().equals(registrationNumber)
                && doctorRepository.existsByRegistrationNumberAndDeletedAtIsNull(registrationNumber)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_REGISTRATION_NUMBER);
        }
        String medicalLicenseNumber = doctorEdit.getMedicalLicenseNumber();
        if (StringUtils.isNotBlank(medicalLicenseNumber)
                && !doctor.getMedicalLicenseNumber().equals(medicalLicenseNumber)
                && doctorRepository.existsByMedicalLicenseNumberAndDeletedAtIsNull(medicalLicenseNumber)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_MEDICAL_LICENSE_NUMBER);
        }
        String email = doctorEdit.getEmail();
        if (StringUtils.isNotBlank(email) && !doctor.getEmail().equals(email)
                && doctorRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_EMAIL);
        }
        String phone = doctorEdit.getPhone();
        if (StringUtils.isNotBlank(phone) && !doctor.getPhone().equals(phone)
                && doctorRepository.existsByPhoneAndDeletedAtIsNull(phone)) {
            throw new DomainException(DomainError.DOCTOR_EXISTS_BY_PHONE);
        }
        doctor.setCategoryCode(doctorEdit.getCategory().getCode());
        doctor.setCode(code);
        doctor.setMedicalLicenseNumber(doctorEdit.getMedicalLicenseNumber());
        doctor.setName(doctorEdit.getName());
        doctor.setRegistrationNumber(doctorEdit.getRegistrationNumber());
        doctor.setAddress(doctorEdit.getAddress());
        doctor.setEmail(doctorEdit.getEmail());
        doctor.setPhone(doctorEdit.getPhone());
        return doctorRepository.save(doctor);
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

    @ForActivity(Activity.GET_DOCTOR_CATEGORY_BY_ID)
    public DoctorCategoryVM getDoctorCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                doctorCategoryRepository.findByIdAndDeletedAtIsNull(id),
                DoctorCategoryVM.class,
                new DomainException(DomainError.DOCTOR_CATEGORY_NOT_FOUND_BY_ID));
    }

}

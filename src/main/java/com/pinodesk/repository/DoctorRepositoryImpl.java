package com.pinodesk.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pinodesk.entity.Doctor;
import com.pinodesk.sequel.repository.AbstractRepository;
import com.pinodesk.sequel.sql.Where;
import com.pinodesk.sequel.utility.SQLUtils;
import com.pinodesk.toolbox.data.ListBuilder;
import com.pinodesk.viewmodel.DoctorFilterVM;
import com.pinodesk.viewmodel.DoctorVM;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DoctorRepositoryImpl extends AbstractRepository<Doctor> implements DoctorRepositoryCustom {

    @Override
    public List<DoctorVM> findByKeyword(String keyword, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select
                    a.*,
                    b.id as category_id,
                    b.name as category_name
                from doctor a
                inner join doctor_category b on b.code = a.category_code and b.language = ?
                where a.deleted_at is null
                """);
        ListBuilder<Object> lb = new ListBuilder<>().add(language);
        if (StringUtils.isNotBlank(keyword)) {
            String likeValue = SQLUtils.likeValueContains(keyword.trim().toLowerCase());
            sb.append("""
                    and (lower(a.name) like ?
                    or a.registration_number like ?
                    or a.medical_license_number like ?
                    or a.phone like ?
                    or lower(a.email) like ?
                    or lower(a.address) like ?
                    or b.code like ?)
                    """);
            lb.add(likeValue).add(likeValue).add(likeValue).add(likeValue).add(likeValue).add(likeValue).add(likeValue);
        }
        return performSelect(sb.toString(), lb.build(), DoctorVM.class);
    }

    @Override
    public List<DoctorVM> findByFilter(DoctorFilterVM filter, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select
                    a.*,
                    b.id as category_id,
                    b.name as category_name
                from doctor a
                inner join doctor_category b on b.code = a.category_code
                """);
        Where where = new Where().isNull("a.deleted_at");
        where.andEquals("b.language", language);
        if (StringUtils.isNotBlank(filter.getName())) {
            where.containsIgnoreCase("a.name", filter.getName());
        }
        if (StringUtils.isNotBlank(filter.getCode())) {
            where.contains("a.code", filter.getCode());
        }
        if (StringUtils.isNotBlank(filter.getRegistrationNumber())) {
            where.containsIgnoreCase("a.registration_number", filter.getRegistrationNumber());
        }
        if (StringUtils.isNotBlank(filter.getMedicalLicenseNumber())) {
            where.containsIgnoreCase("a.medical_license_number", filter.getMedicalLicenseNumber());
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            where.containsIgnoreCase("a.email", filter.getEmail());
        }
        if (StringUtils.isNotBlank(filter.getPhone())) {
            where.containsIgnoreCase("a.phone", filter.getPhone());
        }
        if (StringUtils.isNotBlank(filter.getAddress())) {
            where.containsIgnoreCase("a.address", filter.getAddress());
        }
        if (filter.getCategory() != null) {
            where.equals("b.id", filter.getCategory().getId());
        }
        sb.append(where.getClause());
        return performSelect(sb.toString(), where.getValues(), DoctorVM.class);
    }

}

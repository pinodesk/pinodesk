package com.getkembang.kembangdesktop.service;

import java.util.Date;
import java.util.List;

import com.getkembang.kembangdesktop.constant.CacheName;
import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.domain.SupplierContact;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.SupplierContactRepository;
import com.getkembang.kembangdesktop.repository.SupplierRepository;
import com.getkembang.kembangdesktop.viewmodel.SupplierAddVM;
import com.getkembang.kembangdesktop.viewmodel.SupplierContactAddVM;
import com.getkembang.kembangdesktop.viewmodel.SupplierEditVM;
import com.getkembang.kembangdesktop.viewmodel.SupplierFilterVM;
import com.getkembang.kembangdesktop.viewmodel.SupplierVM;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierService extends BaseService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierContactRepository supplierContactRepository;

    @Cacheable(CacheName.Keys.SUPPLIERS_BY_FILTER)
    public List<SupplierVM> searchSuppliers(SupplierFilterVM filter) {
        return convertList(supplierRepository.filter(filter), SupplierVM.class);
    }

    @CacheEvict(value = CacheName.Keys.SUPPLIERS_BY_FILTER, allEntries = true)
    @Transactional
    public void removeSuppliers(List<Long> ids) {
        supplierRepository.delete(ids);
    }

    @CacheEvict(value = CacheName.Keys.SUPPLIERS_BY_FILTER, allEntries = true)
    @Transactional
    public Long createSupplier(SupplierAddVM supplier, List<SupplierContactAddVM> contacts) {
        if (supplierRepository.existsByCode(supplier.getCode())) {
            throw new DomainException(DomainError.SUPPLIER_EXISTS_BY_CODE);
        }
        String email = supplier.getEmail();
        if (StringUtils.isNotBlank(email) && supplierRepository.existsByEmail(email)) {
            throw new DomainException(DomainError.SUPPLIER_EXISTS_BY_EMAIL);
        }
        String phone = supplier.getPhone();
        if (StringUtils.isNotBlank(phone) && supplierRepository.existsByPhone(phone)) {
            throw new DomainException(DomainError.SUPPLIER_EXISTS_BY_PHONE);
        }
        Long supplierId = supplierRepository.createSupplier(supplier);
        if (CollectionUtils.isNotEmpty(contacts)) {
            contacts.forEach(contact -> createSupplierContact(contact, supplierId));
        }
        return supplierId;
    }

    private Long createSupplierContact(SupplierContactAddVM contact, Long supplierId) {
        String email = contact.getEmail();
        if (StringUtils.isNotBlank(email) && supplierContactRepository.existsByEmailAndSupplierId(email, supplierId)) {
            throw new DomainException(DomainError.SUPPLIER_CONTACT_EXISTS_BY_EMAIL);
        }
        String phone = contact.getPhone();
        if (StringUtils.isNotBlank(phone) && supplierContactRepository.existsByPhoneAndSupplierId(phone, supplierId)) {
            throw new DomainException(DomainError.SUPPLIER_CONTACT_EXISTS_BY_PHONE);
        }
        contact.setSupplierId(supplierId);
        return supplierContactRepository.createSupplierContact(contact);
    }

    @CacheEvict(value = CacheName.Keys.SUPPLIERS_BY_FILTER, allEntries = true)
    @Transactional
    public boolean updateSupplier(SupplierEditVM supplier, List<SupplierContactAddVM> contacts) {
        Long supplierId = supplier.getId();
        if (!supplierRepository.exists(supplierId)) {
            throw new DomainException(DomainError.SUPPLIER_NOT_FOUND_BY_ID);
        }
        if (supplierRepository.existsByCode(supplier.getCode(), supplierId)) {
            throw new DomainException(DomainError.SUPPLIER_OTHER_EXISTS_BY_CODE);
        }
        String email = supplier.getEmail();
        if (StringUtils.isNotBlank(email) && supplierRepository.existsByEmail(email, supplierId)) {
            throw new DomainException(DomainError.SUPPLIER_OTHER_EXISTS_BY_EMAIL);
        }
        String phone = supplier.getPhone();
        if (StringUtils.isNotBlank(phone) && supplierRepository.existsByPhone(phone, supplierId)) {
            throw new DomainException(DomainError.SUPPLIER_OTHER_EXISTS_BY_PHONE);
        }
        supplierContactRepository.delete(new Where().equals(SupplierContact.C_SUPPLIER_ID, supplier.getId()), true);
        if (CollectionUtils.isNotEmpty(contacts)) {
            contacts.forEach(contact -> createSupplierContact(contact, supplierId));
        }
        return supplierRepository.updateSupplier(supplier) == 1;
    }

    public String getNextSupplierCode() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        String maxCode = supplierRepository.findMaxCodeByPrefix(prefix);
        int sequence = 0;
        if (maxCode != null) {
            sequence = Integer.parseInt(maxCode.substring(prefix.length())) + 1;
        }
        return prefix + String.format("%04d", sequence); // Left pad with "0"
    }

    public List<SupplierContactAddVM> getSupplierContacts(Long supplierId) {
        return convertList(
                supplierContactRepository.read(new Where().equals(SupplierContact.C_SUPPLIER_ID, supplierId)),
                SupplierContactAddVM.class);
    }

}

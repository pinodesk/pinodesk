package pinus.desktop.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
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
import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.Supplier;
import pinus.desktop.domain.SupplierContact;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.SupplierContactRepository;
import pinus.desktop.repository.SupplierRepository;
import pinus.desktop.viewmodel.SupplierAddVM;
import pinus.desktop.viewmodel.SupplierContactAddVM;
import pinus.desktop.viewmodel.SupplierEditVM;
import pinus.desktop.viewmodel.SupplierFilterVM;
import pinus.desktop.viewmodel.SupplierVM;

@Service
public class SupplierService extends BaseService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierContactRepository supplierContactRepository;

    @ForActivity(Activity.SEARCH_SUPPLIERS_BY_FILTER)
    @Cacheable(CacheNameConstants.SUPPLIERS_BY_FILTER)
    public List<SupplierVM> searchSuppliers(SupplierFilterVM filter) {
        return objectConverter.convertList(supplierRepository.findByFilter(filter), SupplierVM.class);
    }

    @ForActivity(Activity.SEARCH_RECEIVABLES_BY_KEYWORD)
    @Cacheable(CacheNameConstants.SUPPLIERS_BY_KEYWORD)
    public List<SupplierVM> searchSuppliersByKeyword(String keyword) {
        List<Supplier> suppliers = StringUtils.isBlank(keyword) ?
                supplierRepository.findByDeletedAtIsNull() : supplierRepository.findByKeyword(keyword.trim());
        return objectConverter.convertList(suppliers, SupplierVM.class);
    }

    @ForActivity(Activity.REMOVE_SUPPLIERS)
    @CacheEvict(value = { CacheNameConstants.SUPPLIERS_BY_FILTER, CacheNameConstants.SUPPLIERS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeSuppliers(List<Long> ids) {
        supplierContactRepository.deleteUpdateBySupplierIdIn(ids);
        supplierRepository.deleteUpdateByIdIn(ids);
    }

    @ForActivity(Activity.ADD_SUPPLIER)
    @CacheEvict(value = { CacheNameConstants.SUPPLIERS_BY_FILTER, CacheNameConstants.SUPPLIERS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public Supplier createSupplier(SupplierAddVM supplierAdd, List<SupplierContactAddVM> contacts) {
        if (supplierRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(supplierAdd.getCode())) {
            throw new DomainException(DomainError.SUPPLIER_EXISTS_BY_CODE);
        }
        String email = supplierAdd.getEmail();
        if (StringUtils.isNotBlank(email) && supplierRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new DomainException(DomainError.SUPPLIER_EXISTS_BY_EMAIL);
        }
        String phone = supplierAdd.getPhone();
        if (StringUtils.isNotBlank(phone) && supplierRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(phone)) {
            throw new DomainException(DomainError.SUPPLIER_EXISTS_BY_PHONE);
        }
        Supplier supplier = supplierRepository.save(objectConverter.convertObject(supplierAdd, Supplier.class));
        if (CollectionUtils.isNotEmpty(contacts)) {
            contacts.forEach(contact -> createSupplierContact(contact, supplier.getId()));
        }
        return supplier;
    }

    private SupplierContact createSupplierContact(SupplierContactAddVM contact, Long supplierId) {
        String email = contact.getEmail();
        if (StringUtils.isNotBlank(email) && supplierContactRepository
                .existsByEmailIgnoreCaseAndSupplierIdAndDeletedAtIsNull(email, supplierId)) {
            throw new DomainException(DomainError.SUPPLIER_CONTACT_EXISTS_BY_EMAIL);
        }
        String phone = contact.getPhone();
        if (StringUtils.isNotBlank(phone)
                && supplierContactRepository.existsByPhoneAndSupplierIdAndDeletedAtIsNull(phone, supplierId)) {
            throw new DomainException(DomainError.SUPPLIER_CONTACT_EXISTS_BY_PHONE);
        }
        contact.setSupplierId(supplierId);
        return supplierContactRepository.save(objectConverter.convertObject(contact, SupplierContact.class));
    }

    @ForActivity(Activity.EDIT_SUPPLIER)
    @CacheEvict(value = { CacheNameConstants.SUPPLIERS_BY_FILTER, CacheNameConstants.SUPPLIERS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public Supplier updateSupplier(SupplierEditVM supplierEdit, List<SupplierContactAddVM> contacts) {
        Long supplierId = supplierEdit.getId();
        Supplier supplier = supplierRepository.findByIdAndDeletedAtIsNull(supplierEdit.getId())
                .orElseThrow(() -> new DomainException(DomainError.SUPPLIER_NOT_FOUND_BY_ID));
        if (!supplier.getCode().equals(supplierEdit.getCode())
                && supplierRepository.existsByCodeIgnoreCaseAndDeletedAtIsNull(supplierEdit.getCode())) {
            throw new DomainException(DomainError.SUPPLIER_OTHER_EXISTS_BY_CODE);
        }
        String email = supplierEdit.getEmail();
        if (StringUtils.isNotBlank(email) && !supplier.getEmail().equalsIgnoreCase(email)
                && supplierRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new DomainException(DomainError.SUPPLIER_OTHER_EXISTS_BY_EMAIL);
        }
        String phone = supplierEdit.getPhone();
        if (StringUtils.isNotBlank(phone) && !supplier.getPhone().equals(phone)
                && supplierRepository.existsByPhoneIgnoreCaseAndDeletedAtIsNull(phone)) {
            throw new DomainException(DomainError.SUPPLIER_OTHER_EXISTS_BY_PHONE);
        }
        supplierContactRepository.deleteBySupplierId(supplierId);
        if (CollectionUtils.isNotEmpty(contacts)) {
            contacts.forEach(contact -> createSupplierContact(contact, supplierId));
        }
        supplier.setAddress(supplierEdit.getAddress());
        supplier.setCode(supplierEdit.getCode());
        supplier.setEmail(email);
        supplier.setName(supplierEdit.getName());
        supplier.setPhone(phone);
        supplier.setWebsite(supplierEdit.getWebsite());
        return supplierRepository.save(supplier);
    }

    @ForActivity(Activity.GET_NEXT_SUPPLIER_CODE)
    public String getNextSupplierCode() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        Optional<Supplier> supplier = supplierRepository.findFirstByCodeStartingWithOrderByCodeDesc(prefix);
        int sequence = 0;
        if (supplier.isPresent()) {
            sequence = Integer.parseInt(supplier.get().getCode().substring(prefix.length())) + 1;
        }
        return prefix + String.format("%04d", sequence); // Left pad with "0"
    }

    @ForActivity(Activity.GET_SUPPLIER_CONTACTS)
    public List<SupplierContactAddVM> getSupplierContacts(Long supplierId) {
        return objectConverter.convertList(
                supplierContactRepository.findBySupplierIdAndDeletedAtIsNull(supplierId),
                SupplierContactAddVM.class);
    }

}

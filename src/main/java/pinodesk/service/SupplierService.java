package pinodesk.service;

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

import pinodesk.annotation.TargetActivity;
import pinodesk.constant.Activity;
import pinodesk.constant.CacheNameConstants;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.DomainError;
import pinodesk.entity.Supplier;
import pinodesk.entity.SupplierContact;
import pinodesk.exception.DomainException;
import pinodesk.viewmodel.SupplierAddVM;
import pinodesk.viewmodel.SupplierContactAddVM;
import pinodesk.viewmodel.SupplierEditVM;
import pinodesk.viewmodel.SupplierFilterVM;
import pinodesk.viewmodel.SupplierVM;
import pinodesk.repository.SupplierContactRepository;
import pinodesk.repository.SupplierRepository;

@Service
public class SupplierService extends BaseService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierContactRepository supplierContactRepository;

    @TargetActivity(Activity.SEARCH_SUPPLIERS_BY_FILTER)
    @Cacheable(CacheNameConstants.SUPPLIERS_BY_FILTER)
    public List<SupplierVM> searchSuppliers(SupplierFilterVM filter) {
        return objectConverter.convertList(supplierRepository.findByFilter(filter), SupplierVM.class);
    }

    @TargetActivity(Activity.SEARCH_RECEIVABLES_BY_KEYWORD)
    @Cacheable(CacheNameConstants.SUPPLIERS_BY_KEYWORD)
    public List<SupplierVM> searchSuppliersByKeyword(String keyword) {
        List<Supplier> suppliers = StringUtils.isBlank(keyword) ?
                supplierRepository.findByDeletedAtIsNull() : supplierRepository.findByKeyword(keyword.trim());
        return objectConverter.convertList(suppliers, SupplierVM.class);
    }

    @TargetActivity(Activity.REMOVE_SUPPLIERS)
    @CacheEvict(value = { CacheNameConstants.SUPPLIERS_BY_FILTER, CacheNameConstants.SUPPLIERS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeSuppliers(List<Long> ids) {
        supplierContactRepository.deleteUpdateBySupplierIdIn(ids);
        supplierRepository.deleteUpdateByIdIn(ids);
    }

    @TargetActivity(Activity.ADD_SUPPLIER)
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

    @TargetActivity(Activity.EDIT_SUPPLIER)
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

    @TargetActivity(Activity.GET_NEXT_SUPPLIER_CODE)
    public String getNextSupplierCode() {
        String prefix = DateFormatUtils.format(new Date(), CommonConstants.CODE_PREFIX_DATE_PATTERN);
        Optional<Supplier> supplier = supplierRepository.findFirstByCodeStartingWithOrderByCodeDesc(prefix);
        int sequence = 0;
        if (supplier.isPresent()) {
            sequence = Integer.parseInt(supplier.get().getCode().substring(prefix.length())) + 1;
        }
        return prefix + String.format("%04d", sequence); // Left pad with "0"
    }

    @TargetActivity(Activity.GET_SUPPLIER_CONTACTS)
    public List<SupplierContactAddVM> getSupplierContacts(Long supplierId) {
        return objectConverter.convertList(
                supplierContactRepository.findBySupplierIdAndDeletedAtIsNull(supplierId),
                SupplierContactAddVM.class);
    }

}

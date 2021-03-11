package com.getkembang.kembangdesktop.service;

import java.util.List;

import com.getkembang.kembangdesktop.constant.ContactType;
import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.ContactRepository;
import com.getkembang.kembangdesktop.viewmodel.ContactAddVM;
import com.getkembang.kembangdesktop.viewmodel.ContactEditVM;
import com.getkembang.kembangdesktop.viewmodel.ContactFilterVM;
import com.getkembang.kembangdesktop.viewmodel.ContactVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactService extends BaseService {

    @Autowired
    private ContactRepository contactRepository;

    @Cacheable("contactsByFilter")
    public List<ContactVM> searchContacts(ContactFilterVM filter) {
        return convertList(contactRepository.filter(filter), ContactVM.class);
    }

    @CacheEvict(value = "contactsByFilter", allEntries = true)
    @Transactional
    public Long createContact(ContactAddVM contact) {
        ContactType ct = ContactType.of(contact.getContactType()).orElseThrow();
        if (contactRepository.existsByCodeAndContactType(contact.getCode(), ct)) {
            throw new DomainException(DomainError.CONTACT_EXISTS_BY_CODE_AND_TYPE);
        }
        return contactRepository.createContact(contact);
    }

    @CacheEvict(value = "contactsByFilter", allEntries = true)
    @Transactional
    public boolean updateContact(ContactEditVM contact) {
        ContactType ct = ContactType.of(contact.getContactType()).orElseThrow();
        if (!contactRepository.exists(contact.getId())) {
            throw new DomainException(DomainError.CONTACT_EXISTS_BY_CODE_AND_TYPE);
        }
        if (contactRepository.existsByCodeAndContactType(contact.getCode(), ct, contact.getId())) {
            throw new DomainException(DomainError.CONTACT_EXISTS_BY_CODE_AND_TYPE);
        }
        return contactRepository.updateContact(contact) == 1;
    }

    @CacheEvict(value = "contactsByFilter", allEntries = true)
    @Transactional
    public void removeContacts(List<Long> ids) {
        contactRepository.delete(ids);
    }

}

package com.getkembang.kembangdesktop.service;

import java.util.List;

import com.getkembang.kembangdesktop.repository.ContactRepository;
import com.getkembang.kembangdesktop.viewmodel.ContactFilterVM;
import com.getkembang.kembangdesktop.viewmodel.ContactVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ContactService extends BaseService {

    @Autowired
    private ContactRepository contactRepository;

    @Cacheable("contactsByFilter")
    public List<ContactVM> searchContacts(ContactFilterVM filter) {
        return convertList(contactRepository.filter(filter), ContactVM.class);
    }

}

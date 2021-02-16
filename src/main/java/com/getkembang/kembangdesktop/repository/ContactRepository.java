package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.constant.ContactType;
import com.getkembang.kembangdesktop.domain.Contact;
import com.getkembang.kembangdesktop.viewmodel.ContactAddVM;
import com.getkembang.kembangdesktop.viewmodel.ContactEditVM;
import com.getkembang.kembangdesktop.viewmodel.ContactFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface ContactRepository extends CommonRepository<Contact> {

	List<Contact> filter(ContactFilterVM filter);
    
    Long createContact(ContactAddVM contactAdd);

    Integer updateContact(ContactEditVM contactEdit);

    boolean existsByNameAndContactType(String name, ContactType ct);

    boolean existsByCodeAndContactType(String code, ContactType ct);

}

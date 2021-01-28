package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Contact;
import com.getkembang.kembangdesktop.viewmodel.ContactFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface ContactRepository extends CommonRepository<Contact> {

	List<Contact> filter(ContactFilterVM filter);
    
}

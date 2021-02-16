package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.constant.ContactType;
import com.getkembang.kembangdesktop.domain.Contact;
import com.getkembang.kembangdesktop.viewmodel.ContactAddVM;
import com.getkembang.kembangdesktop.viewmodel.ContactEditVM;
import com.getkembang.kembangdesktop.viewmodel.ContactFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ContactRepositoryImpl extends AbstractRepository<Contact> implements ContactRepository {

    @Override
    public List<Contact> filter(ContactFilterVM filter) {
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getName())) {
            where.containsIgnoreCase(Contact.C_NAME, filter.getName());
        }
        if (StringUtils.isNotBlank(filter.getCode())) {
            where.equals(Contact.C_CODE, filter.getCode());
        }
        if (StringUtils.isNotBlank(filter.getPhone())) {
            where.contains(Contact.C_PHONE, filter.getPhone());
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            where.containsIgnoreCase(Contact.C_EMAIL, filter.getEmail());
        }
        if (StringUtils.isNotBlank(filter.getAddress())) {
            where.containsIgnoreCase(Contact.C_ADDRESS, filter.getAddress());
        }
        if (StringUtils.isNotBlank(filter.getCompanyName())) {
            where.containsIgnoreCase(Contact.C_COMPANY_NAME, filter.getCompanyName());
        }
        if (filter.getContactType() != null) {
            where.equals(Contact.C_CONTACT_TYPE, filter.getContactType());
        }
        return read(where);
    }

    // @formatter:off
    @Override
    public Long createContact(ContactAddVM contactAdd) {
        return executeInsert(new String[] {
            Contact.C_NAME,
            Contact.C_CODE,
            Contact.C_PHONE,
            Contact.C_EMAIL,
            Contact.C_ADDRESS,
            Contact.C_COMPANY_NAME,
            Contact.C_CONTACT_TYPE
        }, new Object[] {
            contactAdd.getName(),
            contactAdd.getCode(),
            contactAdd.getPhone(),
            contactAdd.getEmail(),
            contactAdd.getAddress(),
            contactAdd.getCompanyName(),
            contactAdd.getContactType()
        });
    }

    @Override
    public Integer updateContact(ContactEditVM contactEdit) {
        return update(new String[] {
            Contact.C_NAME,
            Contact.C_CODE,
            Contact.C_PHONE,
            Contact.C_EMAIL,
            Contact.C_ADDRESS,
            Contact.C_COMPANY_NAME,
            Contact.C_CONTACT_TYPE
        }, new Object[] {
            contactEdit.getName(),
            contactEdit.getCode(),
            contactEdit.getPhone(),
            contactEdit.getEmail(),
            contactEdit.getAddress(),
            contactEdit.getCompanyName(),
            contactEdit.getContactType()
        }, contactEdit.getId());
    }
    // @formatter:on

    @Override
    public boolean existsByNameAndContactType(String name, ContactType ct) {
        return exists(
                new Where().equalsIgnoreCase(Contact.C_NAME, name).andEquals(Contact.C_CONTACT_TYPE, ct.toString()));
    }

    @Override
    public boolean existsByCodeAndContactType(String code, ContactType ct) {
        return exists(
                new Where().equalsIgnoreCase(Contact.C_CODE, code).andEquals(Contact.C_CONTACT_TYPE, ct.toString()));
    }

}

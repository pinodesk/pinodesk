package com.getkembang.kembangdesktop.service;

import com.gitlab.muhammadkholidb.toolbox.data.ObjectConverter;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    @Autowired
    protected ObjectConverter objectConverter;

}

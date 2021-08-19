package toska.desktop.service;

import com.gitlab.muhammadkholidb.toolbox.jackson.ObjectConverter;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    @Autowired
    protected ObjectConverter objectConverter;

}

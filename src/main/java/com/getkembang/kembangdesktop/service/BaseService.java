package com.getkembang.kembangdesktop.service;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    @Autowired
    protected ObjectMapper objectMapper;

    protected <T> T convertObject(Object object, Class<T> c) {
        return objectMapper.convertValue(object, c);
    }

    protected <T> T convertOptional(Optional<?> optional, Class<T> c) {
        notNull(optional);
        return optional.map(o -> convertObject(o, c)).orElse(null);
    }

    protected <T, X extends Exception> T convertOptionalOrThrow(Optional<?> optional, Class<T> c, X x) throws X {
        notNull(optional);
        return optional.map(o -> convertObject(o, c)).orElseThrow(() -> x);
    }

    protected <T> List<T> convertList(List<?> list, Class<T> c) {
        notNull(list);
        return list.stream().map(o -> convertObject(o, c)).collect(Collectors.toList());
    }

}

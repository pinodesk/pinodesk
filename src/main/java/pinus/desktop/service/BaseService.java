package pinus.desktop.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import com.gitlab.muhammadkholidb.toolbox.jackson.ObjectConverter;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    @Autowired
    protected ObjectConverter objectConverter;

    @Autowired
    private Validator validator;

    protected <T> void validateConstraints(T object) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}

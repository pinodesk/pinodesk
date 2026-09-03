package com.pinodesk.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.service.SessionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class ServiceAspect {

    @Autowired
    private SessionService sessionService;

    @Around("""
            execution(public * pinodesk.service.*.*(..))
            && !execution(public * pinodesk.service.SessionService.*(..))
            && @annotation(targetActivity)
            """)
    public Object beforeAllServiceMethods(ProceedingJoinPoint call, TargetActivity targetActivity) throws Throwable {
        Object result = null;
        try {
            result = call.proceed();
            if (sessionService.isCurrentSessionActive()) {
                Thread t = new Thread(() -> sessionService.updateLastActivity(targetActivity.value()));
                t.start();
            }
        } catch (Exception e) {
            log.debug("Service method throws exception: {}", e.toString());

            throw e;
        }
        return result;
    }

}

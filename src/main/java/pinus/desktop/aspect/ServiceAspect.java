package pinus.desktop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pinus.desktop.annotation.ForActivity;
import pinus.desktop.service.SessionService;
import pinus.desktop.util.AsyncQueueProcessor;

@Slf4j
@Aspect
@Component
public class ServiceAspect {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AsyncQueueProcessor asyncQueueProcessor;

    @Around("""
            execution(public * pinus.desktop.service.*.*(..))
            && !execution(public * pinus.desktop.service.SessionService.*(..))
            && @annotation(forActivity)
            """)
    public Object beforeAllServiceMethods(ProceedingJoinPoint call, ForActivity forActivity) throws Throwable {
        Object result = null;
        try {
            result = call.proceed();
            if (sessionService.isCurrentSessionActive()) {
                asyncQueueProcessor.process(() -> sessionService.updateLastActivity(forActivity.value()));
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Service method throws exception: {}", e.toString());
            }
            throw e;
        }
        return result;
    }

}

package pinodesk.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pinodesk.annotation.ForActivity;
import pinodesk.service.SessionService;

@Slf4j
@Aspect
@Component
public class ServiceAspect {

    @Autowired
    private SessionService sessionService;

    @Around("""
            execution(public * pinodesk.service.*.*(..))
            && !execution(public * pinodesk.service.SessionService.*(..))
            && @annotation(forActivity)
            """)
    public Object beforeAllServiceMethods(ProceedingJoinPoint call, ForActivity forActivity) throws Throwable {
        Object result = null;
        try {
            result = call.proceed();
            if (sessionService.isCurrentSessionActive()) {
                Thread t = new Thread(() -> sessionService.updateLastActivity(forActivity.value()));
                t.start();
            }
        } catch (Exception e) {
            log.debug("Service method throws exception: {}", e.toString());

            throw e;
        }
        return result;
    }

}

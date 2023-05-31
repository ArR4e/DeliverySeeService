package dev.artur.deliveryfeeservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    //Log events in Controller and Service classes
    @Pointcut("within(com.fujitsu.deliveryfeeservice.controller..*) || within(com.fujitsu.deliveryfeeservice.service..*)")
    public void applicationPointcut() {
    }

    @Before("applicationPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Method: {}.{} executed with arguments: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    @AfterReturning(pointcut = "applicationPointcut()", returning = "returnedValue")
    public void logAfterReturning(JoinPoint joinPoint, Object returnedValue) {
        logger.info("Method {}.{} returned: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                returnedValue
        );
    }

    @AfterThrowing(value = "applicationPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        logger.error("Method {}.{} execution resulted in {}(\"{}\")",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
    }

    //Log return values of JpaRepository::save calls to track persisted WeatherStations
    @AfterReturning(pointcut = "execution(* com.fujitsu.deliveryfeeservice.repository.*.save(..))", returning = "entity")
    public void logAfterPersist(JoinPoint joinPoint, Object entity) {
        logger.info("Entity {} was saved in {}",
                entity,
                joinPoint.getSignature().getDeclaringType().getSimpleName()
        );
    }
}

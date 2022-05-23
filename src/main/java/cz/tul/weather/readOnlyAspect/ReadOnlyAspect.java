package cz.tul.weather.readOnlyAspect;

import cz.tul.weather.exception.ApiRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(prefix = "read-only", name = "mode", havingValue = "true")
public class ReadOnlyAspect {
    @Around("@annotation(ReadOnly)")
    public void disable(ProceedingJoinPoint joinPoint) throws Throwable{
        throw new ApiRequestException("You cant do that in read-only mode!");
    }
}

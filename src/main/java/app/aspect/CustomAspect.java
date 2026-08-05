package app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CustomAspect {

    @Before("@annotation(logAction)")
    public void logAction(LogAction logAction) {

        log.info(logAction.value());
    }
}

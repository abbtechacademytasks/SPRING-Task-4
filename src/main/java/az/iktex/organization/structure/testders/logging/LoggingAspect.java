package az.iktex.organization.structure.testders.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.slf4j.event.Level;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(az.iktex.organization.structure.testders.controller..*)")
    public void controllerLayer() {
    }

    @Pointcut("within(az.iktex.organization.structure.testders.service..*)")
    public void serviceLayer() {
    }

    @Around("controllerLayer() || serviceLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().getDeclaringType().getSimpleName()
                + "." + pjp.getSignature().getName();

        boolean controller = pjp.getSignature().getDeclaringTypeName()
                .startsWith("az.iktex.organization.structure.testders.controller.");

        Level level = controller ? Level.INFO : Level.DEBUG;
        long started = System.nanoTime();

        log.atLevel(level).log(
                "--> {} args={}", method, formatArguments(pjp.getArgs())
        );

        try {
            Object result = pjp.proceed();

            long duration = (System.nanoTime() - started) / 1_000_000;
            log.atLevel(level).log("<-- {} ({} ms)", method, duration);

            return result;
        } catch (Throwable exception) {
            long duration = (System.nanoTime() - started) / 1_000_000;

            log.atLevel(level).log(
                    "<x- {} ({} ms) {}: {}",
                    method,
                    duration,
                    exception.getClass().getSimpleName(),
                    exception.getMessage()
            );

            throw exception;
        }
    }
    private String formatArguments(Object[] arguments) {
        return Arrays.stream(arguments)
                .map(argument -> {
                    if (argument == null) {
                        return "null";
                    }
                    if (argument instanceof Number || argument instanceof Boolean
                            || argument instanceof Enum<?>) {
                        return argument.toString();
                    }
                    return "<" + argument.getClass().getSimpleName() + ">";
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }
}

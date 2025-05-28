package fr.eletutour.loki.chaos.aspect;

import fr.eletutour.asgard.core.JoinPointAwareChaosRule;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class LokiAspect {

    private final List<JoinPointAwareChaosRule> rules;

    public LokiAspect(@Lazy List<JoinPointAwareChaosRule> rules) {
        this.rules = rules;
    }

    @Around("execution(* *(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        for (JoinPointAwareChaosRule rule : rules) {
            if (rule.getTargetClass().equals(pjp.getTarget().getClass().getName())
                    && rule.isEnabled()) {
                rule.applyChaos();
            }
        }

        return pjp.proceed();
    }
}

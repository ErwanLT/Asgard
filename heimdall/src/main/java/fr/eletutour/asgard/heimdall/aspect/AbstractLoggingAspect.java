package fr.eletutour.asgard.heimdall.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractLoggingAspect {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected Object logMethod(ProceedingJoinPoint joinPoint, String annotationType) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String parameters = formatParameters(joinPoint);
        
        logEntry(annotationType, methodName, className, parameters);
        
        try {
            Object result = joinPoint.proceed();
            String resultString = formatResult(result);
            logExit(annotationType, methodName, className, resultString);
            return result;
        } catch (Exception e) {
            logError(annotationType, methodName, className, e);
            throw e;
        }
    }

    private String formatParameters(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            return Arrays.stream(args)
                .map(arg -> {
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return String.valueOf(arg);
                    }
                })
                .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "Impossible de formater les paramètres";
        }
    }

    private String formatResult(Object result) {
        try {
            if (result == null) {
                return "null";
            }
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            return String.valueOf(result);
        }
    }

    protected abstract void logEntry(String annotationType, String methodName, String className, String parameters);
    protected abstract void logExit(String annotationType, String methodName, String className, String result);
    protected abstract void logError(String annotationType, String methodName, String className, Exception e);
} 
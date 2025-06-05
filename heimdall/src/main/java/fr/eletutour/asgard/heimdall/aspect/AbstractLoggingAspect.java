package fr.eletutour.asgard.heimdall.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractLoggingAspect {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected Object logMethod(ProceedingJoinPoint joinPoint, String annotationType) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String parameters = formatParameters(joinPoint); // Keep this for a summarized version

        MDC.put("methodName", methodName);
        MDC.put("className", className);
        MDC.put("annotationType", annotationType);
        MDC.put("parameters", parameters); // Or a more structured version if preferred

        try {
            logEntry(annotationType, methodName, className, parameters); // This can be simplified
            Object result = joinPoint.proceed();
            String resultString = formatResult(result);
            MDC.put("result", resultString); // Add result to MDC
            logExit(annotationType, methodName, className, resultString); // This can be simplified
            return result;
        } catch (Exception e) {
            MDC.put("exceptionClass", e.getClass().getName());
            MDC.put("exceptionMessage", e.getMessage());
            // Consider adding stack trace or part of it if desired, but be mindful of log size
            logError(annotationType, methodName, className, e); // This can be simplified
            throw e;
        } finally {
            MDC.remove("methodName");
            MDC.remove("className");
            MDC.remove("annotationType");
            MDC.remove("parameters");
            MDC.remove("result");
            MDC.remove("exceptionClass");
            MDC.remove("exceptionMessage");
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
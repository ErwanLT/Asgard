package fr.eletutour.asgard.odin.patterns.structural;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.List;

public class ReflectiveDecorator<T> {
    private final T component;
    private final Map<String, List<BiFunction<Method, Object[], Object>>> decorators;
    private final Class<T> interfaceClass;

    public ReflectiveDecorator(T component, Class<T> interfaceClass) {
        if (component == null) {
            throw new NullPointerException("Le composant ne peut pas être null");
        }
        if (interfaceClass == null) {
            throw new NullPointerException("L'interface ne peut pas être null");
        }
        this.component = component;
        this.interfaceClass = interfaceClass;
        this.decorators = new HashMap<>();
    }

    public ReflectiveDecorator<T> decorate(String methodName, BiFunction<Method, Object[], Object> decorator) {
        decorators.computeIfAbsent(methodName, k -> new ArrayList<>()).add(decorator);
        return this;
    }

    @SuppressWarnings("unchecked")
    public T build() {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[] { interfaceClass },
            (proxy, method, args) -> {
                List<BiFunction<Method, Object[], Object>> methodDecorators = decorators.get(method.getName());
                if (methodDecorators != null && !methodDecorators.isEmpty()) {
                    // Applique les décorations dans l'ordre de leur ajout
                    Object result = null;
                    for (BiFunction<Method, Object[], Object> decorator : methodDecorators) {
                        if (result == null) {
                            result = decorator.apply(method, args);
                        } else {
                            // Si on a déjà un résultat, on l'utilise comme argument pour le décorateur suivant
                            result = decorator.apply(method, new Object[]{result});
                        }
                    }
                    return result;
                }
                return method.invoke(component, args);
            }
        );
    }

    // Méthodes utilitaires pour les décorations courantes
    public ReflectiveDecorator<T> logBefore(String methodName) {
        return decorate(methodName, (method, args) -> {
            System.out.println("Avant l'appel de " + method.getName());
            try {
                Object result;
                if (args != null && args.length > 0 && args[0] instanceof String) {
                    result = args[0];
                } else {
                    result = method.invoke(component, args);
                }
                return result;
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("Après l'appel de " + method.getName());
            }
        });
    }

    public ReflectiveDecorator<T> cache(String methodName) {
        Map<String, Object> cache = new HashMap<>();
        return decorate(methodName, (method, args) -> {
            String key = method.getName() + "_" + java.util.Arrays.toString(args);
            return cache.computeIfAbsent(key, k -> {
                try {
                    if (args != null && args.length > 0 && args[0] instanceof String) {
                        return args[0];
                    }
                    return method.invoke(component, args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public ReflectiveDecorator<T> retry(String methodName, int maxAttempts) {
        return decorate(methodName, (method, args) -> {
            Exception lastException = null;
            for (int i = 0; i < maxAttempts; i++) {
                try {
                    if (args != null && args.length > 0 && args[0] instanceof String) {
                        return args[0];
                    }
                    return method.invoke(component, args);
                } catch (Exception e) {
                    lastException = e;
                    if (i < maxAttempts - 1) {
                        try {
                            Thread.sleep(1000 * (i + 1)); // Backoff exponentiel
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(ie);
                        }
                    }
                }
            }
            throw new RuntimeException("Échec après " + maxAttempts + " tentatives", lastException);
        });
    }
} 
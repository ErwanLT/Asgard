package fr.eletutour.asgard.odin.patterns.structural;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectiveAdapter<T> {
    private final Object adaptee;
    private final Class<T> targetInterface;
    private final Map<String, Method> methodCache;

    public ReflectiveAdapter(Object adaptee, Class<T> targetInterface) {
        this.adaptee = adaptee;
        this.targetInterface = targetInterface;
        this.methodCache = new HashMap<>();
        initializeMethodCache();
    }

    private void initializeMethodCache() {
        for (Method targetMethod : targetInterface.getMethods()) {
            try {
                Method adapteeMethod = findMatchingMethod(targetMethod);
                if (adapteeMethod != null) {
                    methodCache.put(targetMethod.getName(), adapteeMethod);
                }
            } catch (NoSuchMethodException e) {
                // Méthode non trouvée, sera gérée lors de l'appel
            }
        }
    }

    private Method findMatchingMethod(Method targetMethod) throws NoSuchMethodException {
        try {
            // Essai direct avec le même nom et les mêmes paramètres
            return adaptee.getClass().getMethod(targetMethod.getName(), targetMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            // Recherche d'une méthode avec un nom différent mais des paramètres compatibles
            for (Method method : adaptee.getClass().getMethods()) {
                if (isCompatibleMethod(method, targetMethod)) {
                    return method;
                }
            }
            throw new NoSuchMethodException("Aucune méthode compatible trouvée pour " + targetMethod.getName());
        }
    }

    private boolean isCompatibleMethod(Method method, Method targetMethod) {
        if (method.getParameterCount() != targetMethod.getParameterCount()) {
            return false;
        }

        Class<?>[] methodParams = method.getParameterTypes();
        Class<?>[] targetParams = targetMethod.getParameterTypes();

        for (int i = 0; i < methodParams.length; i++) {
            if (!methodParams[i].isAssignableFrom(targetParams[i])) {
                return false;
            }
        }

        return targetMethod.getReturnType().isAssignableFrom(method.getReturnType());
    }

    @SuppressWarnings("unchecked")
    public T adapt() {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
            targetInterface.getClassLoader(),
            new Class<?>[] { targetInterface },
            (proxy, method, args) -> {
                Method adapteeMethod = methodCache.get(method.getName());
                if (adapteeMethod == null) {
                    throw new UnsupportedOperationException(
                        "Méthode " + method.getName() + " non supportée par l'adaptateur");
                }
                return adapteeMethod.invoke(adaptee, args);
            }
        );
    }
} 
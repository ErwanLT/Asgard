package fr.eletutour.asgard.odin.patterns.structural;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ReflectiveComposite<T> implements Composite<T> {
    private final List<T> children;
    private final Map<String, Method> methodCache;

    public ReflectiveComposite(Class<T> componentType) {
        this.children = new ArrayList<>();
        this.methodCache = new HashMap<>();
        initializeMethodCache(componentType);
    }

    private void initializeMethodCache(Class<T> componentType) {
        for (Method method : componentType.getMethods()) {
            if (method.getParameterCount() == 0) {
                methodCache.put(method.getName(), method);
            }
        }
    }

    @Override
    public void add(T component) {
        children.add(component);
    }

    @Override
    public void remove(T component) {
        children.remove(component);
    }

    @Override
    public List<T> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public void operation() {
        // Exécute l'opération sur tous les enfants
        for (T child : children) {
            try {
                // Par défaut, on essaie d'appeler une méthode 'operation' si elle existe
                Method operationMethod = methodCache.get("operation");
                if (operationMethod != null) {
                    operationMethod.invoke(child);
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de l'exécution de l'opération sur " + child, e);
            }
        }
    }
} 
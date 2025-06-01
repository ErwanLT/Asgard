package fr.eletutour.asgard.odin.patterns.creational;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectiveBuilder<T> {
    private final Class<T> targetClass;
    private final Map<String, Object> properties;
    private final Map<String, Method> setters;

    public ReflectiveBuilder(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.properties = new HashMap<>();
        this.setters = new HashMap<>();
        initializeSetters();
    }

    private void initializeSetters() {
        for (Method method : targetClass.getMethods()) {
            if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                String propertyName = method.getName().substring(3);
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                setters.put(propertyName, method);
            }
        }
    }

    public ReflectiveBuilder<T> with(String propertyName, Object value) {
        if (!setters.containsKey(propertyName)) {
            throw new IllegalArgumentException("Propriété '" + propertyName + "' non trouvée dans la classe " + targetClass.getName());
        }
        properties.put(propertyName, value);
        return this;
    }

    public T build() {
        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                Method setter = setters.get(entry.getKey());
                setter.invoke(instance, entry.getValue());
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la construction de l'objet " + targetClass.getName(), e);
        }
    }
} 
package fr.eletutour.asgard.odin.patterns.creational;

import java.lang.reflect.Constructor;

public class ReflectiveFactory<T> implements AbstractFactory<T> {
    private final Class<T> baseClass;

    public ReflectiveFactory(Class<T> baseClass) {
        this.baseClass = baseClass;
    }

    @Override
    public T create(String type) {
        if (type == null) {
            throw new NullPointerException("Le nom de la classe ne peut pas être null");
        }
        
        try {
            String fullClassName = baseClass.getPackage().getName() + "." + type;
            Class<?> concreteClass = Class.forName(fullClassName);
            
            // Vérifie que la classe implémente bien l'interface attendue
            if (!baseClass.isInterface() || !baseClass.isAssignableFrom(concreteClass)) {
                throw new RuntimeException("La classe " + type + " n'implémente pas l'interface " + baseClass.getName());
            }
            
            Constructor<?> constructor = concreteClass.getDeclaredConstructor();
            return (T) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de l'objet de type " + type, e);
        }
    }
} 
package fr.eletutour.asgard.odin.patterns.creational;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation du pattern Prototype utilisant la réflexion.
 * Permet de cloner des objets de manière dynamique.
 */
public class ReflectivePrototype<T> {
    private final T prototype;
    private final Map<String, Object> customizations;
    private final Class<T> prototypeClass;

    /**
     * Constructeur du prototype.
     *
     * @param prototype L'objet prototype à cloner
     * @param prototypeClass La classe du prototype
     */
    public ReflectivePrototype(T prototype, Class<T> prototypeClass) {
        if (prototype == null) {
            throw new NullPointerException("Le prototype ne peut pas être null");
        }
        if (prototypeClass == null) {
            throw new NullPointerException("La classe du prototype ne peut pas être null");
        }
        this.prototype = prototype;
        this.prototypeClass = prototypeClass;
        this.customizations = new HashMap<>();
    }

    /**
     * Personnalise une propriété du prototype.
     *
     * @param propertyName Le nom de la propriété
     * @param value La nouvelle valeur
     * @return L'instance courante pour le chaînage
     */
    public ReflectivePrototype<T> customize(String propertyName, Object value) {
        customizations.put(propertyName, value);
        return this;
    }

    /**
     * Crée un clone du prototype avec les personnalisations.
     *
     * @return Un nouvel objet cloné
     */
    @SuppressWarnings("unchecked")
    public T clone() {
        try {
            // Crée une nouvelle instance
            Constructor<T> constructor = prototypeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T clone = constructor.newInstance();

            // Copie les propriétés du prototype
            for (java.lang.reflect.Field field : prototypeClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(prototype);
                field.set(clone, value);
            }

            // Applique les personnalisations
            for (Map.Entry<String, Object> entry : customizations.entrySet()) {
                java.lang.reflect.Field field = prototypeClass.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(clone, entry.getValue());
            }

            return clone;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchFieldException e) {
            throw new RuntimeException("Erreur lors du clonage du prototype", e);
        }
    }

    /**
     * Réinitialise les personnalisations.
     *
     * @return L'instance courante pour le chaînage
     */
    public ReflectivePrototype<T> reset() {
        customizations.clear();
        return this;
    }
} 
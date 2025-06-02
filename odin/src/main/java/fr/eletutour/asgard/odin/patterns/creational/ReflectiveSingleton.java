package fr.eletutour.asgard.odin.patterns.creational;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implémentation du pattern Singleton utilisant la réflexion.
 * Permet de garantir une seule instance d'une classe.
 */
public class ReflectiveSingleton {
    private static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    private ReflectiveSingleton() {
        // Constructeur privé pour empêcher l'instanciation directe
    }

    /**
     * Obtient l'instance unique d'une classe.
     *
     * @param <T> Le type de l'instance
     * @param clazz La classe dont on veut l'instance
     * @return L'instance unique
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        return (T) instances.computeIfAbsent(clazz, ReflectiveSingleton::createInstance);
    }

    /**
     * Crée une nouvelle instance d'une classe.
     *
     * @param clazz La classe à instancier
     * @return La nouvelle instance
     */
    private static Object createInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException e) {
            throw new RuntimeException("Erreur lors de la création de l'instance de " + clazz.getName(), e);
        }
    }

    /**
     * Réinitialise l'instance d'une classe.
     *
     * @param clazz La classe dont on veut réinitialiser l'instance
     */
    public static void resetInstance(Class<?> clazz) {
        instances.remove(clazz);
    }

    /**
     * Réinitialise toutes les instances.
     */
    public static void resetAll() {
        instances.clear();
    }

    /**
     * Vérifie si une instance existe pour une classe.
     *
     * @param clazz La classe à vérifier
     * @return true si une instance existe, false sinon
     */
    public static boolean hasInstance(Class<?> clazz) {
        return instances.containsKey(clazz);
    }
} 